const argon2 = require("argon2");
const jwt = require("jsonwebtoken");
const { Op } = require("sequelize");

const sequelize = require("../../configs/database");
const User = require("../../models/User");
const {
    registerSchema,
    loginSchema,
} = require("./auth.validation");

function createAccessToken(user) {
    return jwt.sign(
        { username: user.username },
        process.env.JWT_ACCESS_SECRET,
        {
            subject: String(user.id),
            expiresIn: process.env.JWT_ACCESS_EXPIRES_IN || "15m",
            issuer: "day-flow-api",
            audience: "day-flow",
        }
    );
}

function publicUser(user) {
    return {
        id: user.id,
        username: user.username,
        email: user.email,
        createdAt: user.createdAt,
    };
}

async function register(req, res, next) {
    try {
        const result = registerSchema.safeParse(req.body);

        if (!result.success) {
            return res.status(400).json({
                message: "Validation failed",
                errors: result.error.issues.map((issue) => ({
                    field: issue.path.join("."),
                    message: issue.message,
                })),
            });
        }

        const { username, email, password } = result.data;

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

        const user = await sequelize.transaction(async (transaction) => {
            return User.create(
                { username, email, passwordHash },
                { transaction }
            );
        });

        const accessToken = createAccessToken(user);

        return res.status(201).json({
            message: "Registration successful",
            user: publicUser(user),
            accessToken,
        });
    } catch (error) {
        // Handles concurrent registration attempts with the same username/email.
        if (error.name === "SequelizeUniqueConstraintError") {
            return res.status(409).json({
                message: "Username or email is already registered",
            });
        }

        next(error);
    }
}

async function login(req, res, next) {
    try {
        const result = loginSchema.safeParse(req.body);

        if (!result.success) {
            return res.status(400).json({
                message: "Validation failed",
                errors: result.error.issues.map((issue) => ({
                    field: issue.path.join("."),
                    message: issue.message,
                })),
            });
        }

        const { identifier, password } = result.data;
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

        const accessToken = createAccessToken(user);

        return res.status(200).json({
            message: "Login successful",
            user: publicUser(user),
            accessToken,
        });
    } catch (error) {
        next(error);
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
        next(error);
    }
}

module.exports = {
    register,
    login,
    me,
};