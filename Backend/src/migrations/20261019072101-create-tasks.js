"use strict";

module.exports = {
    async up(queryInterface, Sequelize) {
        await queryInterface.createTable("tasks", {
            id: {
                type: Sequelize.INTEGER.UNSIGNED,
                primaryKey: true,
                autoIncrement: true,
                allowNull: false,
            },

            user_id: {
                type: Sequelize.INTEGER.UNSIGNED,
                allowNull: false,
                references: {
                    model: "users",
                    key: "id",
                },
                onUpdate: "CASCADE",
                onDelete: "CASCADE",
            },

            group_id: {
                type: Sequelize.INTEGER.UNSIGNED,
                allowNull: true,
                references: {
                    model: "groups",
                    key: "id",
                },
                onUpdate: "CASCADE",
                onDelete: "SET NULL",
            },

            title: {
                type: Sequelize.STRING(200),
                allowNull: false,
            },

            description: {
                type: Sequelize.TEXT,
                allowNull: true,
            },

            status: {
                type: Sequelize.ENUM("pending", "in_progress", "completed"),
                allowNull: false,
                defaultValue: "pending",
            },

            priority: {
                type: Sequelize.ENUM("low", "medium", "high"),
                allowNull: false,
                defaultValue: "medium",
            },

            due_date: {
                type: Sequelize.DATE,
                allowNull: true,
            },

            created_at: {
                type: Sequelize.DATE,
                allowNull: false,
                defaultValue: Sequelize.literal("CURRENT_TIMESTAMP"),
            },

            updated_at: {
                type: Sequelize.DATE,
                allowNull: false,
                defaultValue: Sequelize.literal(
                    "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",
                ),
            },
        });

        await queryInterface.addIndex("tasks", ["user_id"]);
        await queryInterface.addIndex("tasks", ["group_id"]);
        await queryInterface.addIndex("tasks", ["user_id", "status"]);
        await queryInterface.addIndex("tasks", ["user_id", "due_date"]);
    },

    async down(queryInterface) {
        await queryInterface.dropTable("tasks");
    },
};
