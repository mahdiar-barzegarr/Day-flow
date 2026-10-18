const jwt = require("jsonwebtoken");

function authenticate(req, res, next) {
    const authorization = req.headers.authorization;

    if (!authorization || !authorization.startsWith("Bearer ")) {
        return res.status(401).json({
            message: "Authentication required",
        });
    }

    const token = authorization.slice(7).trim();

    if (!token) {
        return res.status(401).json({
            message: "Invalid access token",
        });
    }

    try {
        const payload = jwt.verify(token, process.env.JWT_ACCESS_SECRET, {
            issuer: "day-flow-api",
            audience: "day-flow",
        });

        req.user = {
            id: Number(payload.sub),
            username: payload.username,
        };

        if (!Number.isSafeInteger(req.user.id) || req.user.id <= 0) {
            return res.status(401).json({
                message: "Invalid access token",
            });
        }

        next();
    } catch {
        return res.status(401).json({
            message: "Invalid or expired access token",
        });
    }
}

module.exports = authenticate;