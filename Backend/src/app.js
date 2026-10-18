require("dotenv").config();

const pool = require("./src/configs/database");
const app = require("./app");

const PORT = process.env.PORT || 3000;

async function startServer() {
    try {
        const connection = await pool.getConnection();
        await connection.ping();
        connection.release();

        console.log("MySQL connected successfully");

        app.listen(PORT, () => {
            console.log(`Server running on http://localhost:${PORT}`);
        });
    } catch (error) {
        console.error("Failed to start server:", error);
        await pool.end();
        process.exit(1);
    }
}

startServer();
