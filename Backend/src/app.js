const express = require("express");
const cors = require("cors");
const helmet = require("helmet");
const swaggerUi = require("swagger-ui-express");

const authRoutes = require("./modules/auth/auth.routes");
const groupRoutes = require("./modules/groups/groups.routes");
const swaggerSpec = require("./configs/swagger");

const app = express();

// Security headers
app.use(helmet());

// CORS
app.use(cors());

// Parse JSON request bodies
app.use(express.json({ limit: "10kb" }));

// Health check
app.get("/", (req, res) => {
    res.status(200).json({
        success: true,
        message: "Day-Flow API is running",
    });
});

// Swagger documentation
app.get("/api-docs.json", (req, res) => {
    res.json(swaggerSpec);
});

app.use(
    "/api-docs",
    swaggerUi.serve,
    swaggerUi.setup(swaggerSpec, {
        explorer: true,
        swaggerOptions: {
            persistAuthorization: true,
        },
    }),
);

// Auth routes
app.use("/api/auth", authRoutes);

// Group routes
app.use("/api/groups", groupRoutes);

// Handle unknown routes
app.use((req, res) => {
    res.status(404).json({
        success: false,
        message: "Route not found",
    });
});

// Global error handler
app.use((error, req, res, next) => {
    console.error(error);

    if (res.headersSent) {
        return next(error);
    }

    res.status(500).json({
        success: false,
        message: "Internal server error",
    });
});

module.exports = app;
