const { DataTypes } = require("sequelize");
const sequelize = require("../configs/database");

const User = sequelize.define(
    "User",
    {
        id: {
            type: DataTypes.INTEGER.UNSIGNED,
            primaryKey: true,
            autoIncrement: true,
        },

        username: {
            type: DataTypes.STRING(30),
            allowNull: false,
            unique: true,
        },

        email: {
            type: DataTypes.STRING(254),
            allowNull: false,
            unique: true,
            set(value) {
                this.setDataValue("email", value.toLowerCase().trim());
            },
        },

        passwordHash: {
            type: DataTypes.STRING(255),
            allowNull: false,
            field: "password_hash",
        },
    },
    {
        tableName: "users",
        timestamps: true,
        underscored: true,
        defaultScope: {
            attributes: { exclude: ["passwordHash"] },
        },
        scopes: {
            withPassword: {
                attributes: { include: ["passwordHash"] },
            },
        },
    }
);

module.exports = User;