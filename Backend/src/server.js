require("dotenv").config();

const app = require("./app");
const sequelize = require("./configs/database");

// Register Sequelize models
require("./models/User");

const PORT = Number(process.env.PORT) || 3000;

async function startServer() {
    try {
        if (!process.env.JWT_ACCESS_SECRET) {
            throw new Error("JWT_ACCESS_SECRET is missing from .env");
        }

        await sequelize.authenticate();

        console.log("MySQL connected successfully");

        app.listen(PORT, () => {
            console.log(`Server running on http://localhost:${PORT}`);
        });
    } catch (error) {
        console.error("Failed to start server:", error.message);

        await sequelize.close().catch(() => {});

        process.exitCode = 1;
    }
}

startServer();
