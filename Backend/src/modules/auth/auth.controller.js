const argon2 = require("argon2");
const jwt = require("jsonwebtoken");
const crypto = require("node:crypto");
const { Op } = require("sequelize");

const sequelize = require("../../configs/database");
const User = require("../../models/User");
const RefreshToken = require("../../models/RefreshToken");

const REFRESH_TOKEN_EXPIRES_IN_DAYS = 7;

function createAccessToken(user) {
    return jwt.sign(
        { username: user.username },
        process.env.JWT_ACCESS_SECRET,
        {
            subject: String(user.id),
            expiresIn: process.env.JWT_ACCESS_EXPIRES_IN || "15m",
            issuer: "day-flow-api",
            audience: "day-flow",
        },
    );
}

function generateRefreshToken() {
    return crypto.randomBytes(48).toString("base64url");
}

function hashRefreshToken(token) {
    return crypto.createHash("sha256").update(token).digest("hex");
}

function publicUser(user) {
    return {
        id: user.id,
        username: user.username,
        email: user.email,
        createdAt: user.createdAt,
    };
}

async function issueRefreshToken(user, transaction) {
    const refreshToken = generateRefreshToken();

    const expiresAt = new Date(
        Date.now() + REFRESH_TOKEN_EXPIRES_IN_DAYS * 24 * 60 * 60 * 1000,
    );

    await RefreshToken.create(
        {
            userId: user.id,
            tokenHash: hashRefreshToken(refreshToken),
            expiresAt,
        },
        { transaction },
    );

    return refreshToken;
}

async function issueTokenPair(user, transaction) {
    const accessToken = createAccessToken(user);
    const refreshToken = await issueRefreshToken(user, transaction);

    return {
        accessToken,
        refreshToken,
    };
}

async function register(req, res, next) {
    try {
        // Already validated by the validate middleware.
        const { username, email, password } = req.body;

        const existingUser = await User.findOne({
            where: {
                [Op.or]: [{ username }, { email }],
            },
        });

        if (existingUser) {
            const field =
                existingUser.username === username ? "username" : "email";

            return res.status(409).json({
                message: `${field} is already registered`,
            });
        }

        const passwordHash = await argon2.hash(password);

        const result = await sequelize.transaction(async (transaction) => {
            const user = await User.create(
                { username, email, passwordHash },
                { transaction },
            );

            const tokens = await issueTokenPair(user, transaction);

            return {
                user,
                ...tokens,
            };
        });

        return res.status(201).json({
            message: "Registration successful",
            user: publicUser(result.user),
            accessToken: result.accessToken,
            refreshToken: result.refreshToken,
        });
    } catch (error) {
        // Handles concurrent registration attempts.
        if (error.name === "SequelizeUniqueConstraintError") {
            return res.status(409).json({
                message: "Username or email is already registered",
            });
        }

        return next(error);
    }
}

async function login(req, res, next) {
    try {
        // Already validated by the validate middleware.
        const { identifier, password } = req.body;
        const normalizedIdentifier = identifier.toLowerCase();

        const user = await User.scope("withPassword").findOne({
            where: {
                [Op.or]: [
                    { email: normalizedIdentifier },
                    { username: identifier },
                ],
            },
        });

        if (!user || !(await argon2.verify(user.passwordHash, password))) {
            return res.status(401).json({
                message: "Invalid email/username or password",
            });
        }

        const tokens = await sequelize.transaction(async (transaction) => {
            return issueTokenPair(user, transaction);
        });

        return res.status(200).json({
            message: "Login successful",
            user: publicUser(user),
            accessToken: tokens.accessToken,
            refreshToken: tokens.refreshToken,
        });
    } catch (error) {
        return next(error);
    }
}

async function refresh(req, res, next) {
    try {
        const { refreshToken } = req.body;

        if (typeof refreshToken !== "string" || !refreshToken) {
            return res.status(400).json({
                message: "Refresh token is required",
            });
        }

        const tokenHash = hashRefreshToken(refreshToken);

        const result = await sequelize.transaction(async (transaction) => {
            const storedToken = await RefreshToken.findOne({
                where: { tokenHash },
                transaction,
                lock: transaction.LOCK.UPDATE,
            });

            if (!storedToken) {
                return { error: "INVALID_REFRESH_TOKEN" };
            }

            if (
                storedToken.revokedAt ||
                new Date(storedToken.expiresAt).getTime() <= Date.now()
            ) {
                return { error: "INVALID_REFRESH_TOKEN" };
            }

            const user = await User.findByPk(storedToken.userId, {
                transaction,
            });

            if (!user) {
                storedToken.revokedAt = new Date();
                await storedToken.save({ transaction });

                return { error: "INVALID_REFRESH_TOKEN" };
            }

            // Revoke the old token before issuing a replacement.
            storedToken.revokedAt = new Date();
            await storedToken.save({ transaction });

            const tokens = await issueTokenPair(user, transaction);

            return {
                accessToken: tokens.accessToken,
                refreshToken: tokens.refreshToken,
            };
        });

        if (result.error) {
            return res.status(401).json({
                message: "Invalid or expired refresh token",
            });
        }

        return res.status(200).json({
            message: "Token refreshed successfully",
            accessToken: result.accessToken,
            refreshToken: result.refreshToken,
        });
    } catch (error) {
        return next(error);
    }
}

async function logout(req, res, next) {
    try {
        const { refreshToken } = req.body;

        if (typeof refreshToken !== "string" || !refreshToken) {
            return res.status(400).json({
                message: "Refresh token is required",
            });
        }

        await RefreshToken.update(
            { revokedAt: new Date() },
            {
                where: {
                    tokenHash: hashRefreshToken(refreshToken),
                    revokedAt: null,
                },
            },
        );

        return res.status(200).json({
            message: "Logged out successfully",
        });
    } catch (error) {
        return next(error);
    }
}

async function me(req, res, next) {
    try {
        const user = await User.findByPk(req.user.id);

        if (!user) {
            return res.status(404).json({
                message: "User not found",
            });
        }

        return res.status(200).json({
            user: publicUser(user),
        });
    } catch (error) {
        return next(error);
    }
}

module.exports = {
    register,
    login,
    refresh,
    logout,
    me,
};
