"use strict";

module.exports = {
    async up(queryInterface, Sequelize) {
        await queryInterface.createTable("groups", {
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

            name: {
                type: Sequelize.STRING(100),
                allowNull: false,
            },

            color: {
                type: Sequelize.STRING(7),
                allowNull: false,
                defaultValue: "#3B82F6",
            },

            icon: {
                type: Sequelize.STRING(50),
                allowNull: false,
                defaultValue: "folder",
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

        await queryInterface.addIndex("groups", ["user_id"]);
    },

    async down(queryInterface) {
        await queryInterface.dropTable("groups");
    },
};
