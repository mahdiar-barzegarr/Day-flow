require("dotenv").config();

const app = require("./app");
const sequelize = require("./configs/database");

const PORT = process.env.PORT || 3000;

async function startServer() {
    try {
        await sequelize.authenticate();

        console.log("MySQL connected successfully");

        app.listen(PORT, () => {
            console.log(`Server running on http://localhost:${PORT}`);
        });
    } catch (error) {
        console.error("Failed to start server:", error);
        await sequelize.close();
        process.exit(1);
    }
}

startServer();