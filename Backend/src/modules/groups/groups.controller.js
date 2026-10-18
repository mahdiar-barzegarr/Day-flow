const { Op } = require("sequelize");
const Group = require("../../models/Group");

const listGroups = async (req, res) => {
    try {
        const groups = await Group.findAll({
            where: {
                userId: req.user.id,
            },
            order: [["createdAt", "DESC"]],
        });

        return res.status(200).json({
            success: true,
            data: groups,
        });
    } catch (error) {
        console.error("List groups error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to retrieve groups",
        });
    }
};

const getGroup = async (req, res) => {
    try {
        const group = await Group.findOne({
            where: {
                id: req.params.id,
                userId: req.user.id,
            },
        });

        if (!group) {
            return res.status(404).json({
                success: false,
                message: "Group not found",
            });
        }

        return res.status(200).json({
            success: true,
            data: group,
        });
    } catch (error) {
        console.error("Get group error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to retrieve group",
        });
    }
};

const createGroup = async (req, res) => {
    try {
        const { name, color, icon } = req.body;

        const group = await Group.create({
            name,
            color,
            icon,
            userId: req.user.id,
        });

        return res.status(201).json({
            success: true,
            message: "Group created successfully",
            data: group,
        });
    } catch (error) {
        console.error("Create group error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to create group",
        });
    }
};

const updateGroup = async (req, res) => {
    try {
        const group = await Group.findOne({
            where: {
                id: req.params.id,
                userId: req.user.id,
            },
        });

        if (!group) {
            return res.status(404).json({
                success: false,
                message: "Group not found",
            });
        }

        await group.update(req.body);

        return res.status(200).json({
            success: true,
            message: "Group updated successfully",
            data: group,
        });
    } catch (error) {
        console.error("Update group error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to update group",
        });
    }
};

const deleteGroup = async (req, res) => {
    try {
        const group = await Group.findOne({
            where: {
                id: req.params.id,
                userId: req.user.id,
            },
        });

        if (!group) {
            return res.status(404).json({
                success: false,
                message: "Group not found",
            });
        }

        await group.destroy();

        return res.status(200).json({
            success: true,
            message: "Group deleted successfully",
        });
    } catch (error) {
        console.error("Delete group error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to delete group",
        });
    }
};

module.exports = {
    listGroups,
    getGroup,
    createGroup,
    updateGroup,
    deleteGroup,
};
