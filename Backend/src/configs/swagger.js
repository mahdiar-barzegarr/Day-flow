const swaggerJsdoc = require("swagger-jsdoc");
const path = require("path");

const options = {
    definition: {
        openapi: "3.0.3",

        info: {
            title: "Day-Flow API",
            version: "1.0.0",
            description: "API documentation for the Day-Flow application.",
        },

        servers: [
            {
                url: "http://localhost:3000",
                description: "Local development server",
            },
        ],

        components: {
            securitySchemes: {
                BearerAuth: {
                    type: "http",
                    scheme: "bearer",
                    bearerFormat: "JWT",
                    description: "Enter your access token.",
                },
            },
        },
    },

    apis: [path.join(__dirname, "../docs/*.swagger.js").replace(/\\/g, "/")],
};

const swaggerSpec = swaggerJsdoc(options);

module.exports = swaggerSpec;
