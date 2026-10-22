const { DataTypes } = require("sequelize");
const sequelize = require("../configs/database");

const Task = sequelize.define(
    "Task",
    {
        id: {
            type: DataTypes.INTEGER.UNSIGNED,
            primaryKey: true,
            autoIncrement: true,
        },

        userId: {
            type: DataTypes.INTEGER.UNSIGNED,
            allowNull: false,
            field: "user_id",
            references: {
                model: "users",
                key: "id",
            },
        },

        groupId: {
            type: DataTypes.INTEGER.UNSIGNED,
            allowNull: true,
            field: "group_id",
            references: {
                model: "groups",
                key: "id",
            },
        },

        title: {
            type: DataTypes.STRING(200),
            allowNull: false,
            validate: {
                notEmpty: true,
                len: [1, 200],
            },
        },

        description: {
            type: DataTypes.TEXT,
            allowNull: true,
        },

        status: {
            type: DataTypes.ENUM("pending", "in_progress", "completed"),
            allowNull: false,
            defaultValue: "pending",
        },

        priority: {
            type: DataTypes.ENUM("low", "medium", "high"),
            allowNull: false,
            defaultValue: "medium",
        },

        dueDate: {
            type: DataTypes.DATE,
            allowNull: true,
            field: "due_date",
        },
    },
    {
        tableName: "tasks",
        timestamps: true,
        underscored: true,
    },
);

module.exports = Task;
