const { DataTypes } = require("sequelize");
const sequelize = require("../configs/database");

const Group = sequelize.define(
    "Group",
    {
        id: {
            type: DataTypes.INTEGER,
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

        name: {
            type: DataTypes.STRING(100),
            allowNull: false,
            validate: {
                notEmpty: true,
                len: [1, 100],
            },
        },

        color: {
            type: DataTypes.STRING(7),
            allowNull: false,
            defaultValue: "#3B82F6",
        },

        icon: {
            type: DataTypes.STRING(50),
            allowNull: false,
            defaultValue: "folder",
        },
    },
    {
        tableName: "groups",
        timestamps: true,
        underscored: true,
    },
);

module.exports = Group;
