const { DataTypes } = require("sequelize");
const sequelize = require("../configs/database");

const RefreshToken = sequelize.define(
    "RefreshToken",
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
        },
        tokenHash: {
            type: DataTypes.STRING(64),
            allowNull: false,
            unique: true,
            field: "token_hash",
        },
        expiresAt: {
            type: DataTypes.DATE,
            allowNull: false,
            field: "expires_at",
        },
        revokedAt: {
            type: DataTypes.DATE,
            allowNull: true,
            field: "revoked_at",
        },
    },
    {
        tableName: "refresh_tokens",
        timestamps: true,
        underscored: true,
    },
);

module.exports = RefreshToken;
