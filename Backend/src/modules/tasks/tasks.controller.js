const Task = require("../../models/Task");
const Group = require("../../models/Group");

const getTaskId = (req) => {
    const id = Number(req.params.id);

    if (!Number.isSafeInteger(id) || id <= 0) {
        return null;
    }

    return id;
};

const listTasks = async (req, res) => {
    try {
        const { status, priority, groupId } = req.query;

        const where = {
            userId: req.user.id,
        };

        if (status) {
            where.status = status;
        }

        if (priority) {
            where.priority = priority;
        }

        if (groupId !== undefined) {
            const parsedGroupId = Number(groupId);

            if (
                groupId === "" ||
                !Number.isSafeInteger(parsedGroupId) ||
                parsedGroupId <= 0
            ) {
                return res.status(400).json({
                    success: false,
                    message: "Invalid groupId",
                });
            }

            where.groupId = parsedGroupId;
        }

        const tasks = await Task.findAll({
            where,
            order: [
                ["createdAt", "DESC"],
                ["id", "DESC"],
            ],
        });

        return res.status(200).json({
            success: true,
            data: tasks,
        });
    } catch (error) {
        console.error("List tasks error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to retrieve tasks",
        });
    }
};

const getTask = async (req, res) => {
    try {
        const id = getTaskId(req);

        if (!id) {
            return res.status(400).json({
                success: false,
                message: "Invalid task ID",
            });
        }

        const task = await Task.findOne({
            where: {
                id,
                userId: req.user.id,
            },
        });

        if (!task) {
            return res.status(404).json({
                success: false,
                message: "Task not found",
            });
        }

        return res.status(200).json({
            success: true,
            data: task,
        });
    } catch (error) {
        console.error("Get task error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to retrieve task",
        });
    }
};

const createTask = async (req, res) => {
    try {
        const { title, description, groupId, status, priority, dueDate } =
            req.body;

        // A group must belong to the current user.
        if (groupId != null) {
            const group = await Group.findOne({
                where: {
                    id: groupId,
                    userId: req.user.id,
                },
            });

            if (!group) {
                return res.status(400).json({
                    success: false,
                    message: "Group not found or does not belong to you",
                });
            }
        }

        const task = await Task.create({
            title,
            description,
            groupId,
            status,
            priority,
            dueDate,
            userId: req.user.id,
        });

        return res.status(201).json({
            success: true,
            message: "Task created successfully",
            data: task,
        });
    } catch (error) {
        console.error("Create task error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to create task",
        });
    }
};

const updateTask = async (req, res) => {
    try {
        const id = getTaskId(req);

        if (!id) {
            return res.status(400).json({
                success: false,
                message: "Invalid task ID",
            });
        }

        const task = await Task.findOne({
            where: {
                id,
                userId: req.user.id,
            },
        });

        if (!task) {
            return res.status(404).json({
                success: false,
                message: "Task not found",
            });
        }

        // Validate ownership if a new group is assigned.
        if (req.body.groupId != null) {
            const group = await Group.findOne({
                where: {
                    id: req.body.groupId,
                    userId: req.user.id,
                },
            });

            if (!group) {
                return res.status(400).json({
                    success: false,
                    message: "Group not found or does not belong to you",
                });
            }
        }

        await task.update(req.body);

        return res.status(200).json({
            success: true,
            message: "Task updated successfully",
            data: task,
        });
    } catch (error) {
        console.error("Update task error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to update task",
        });
    }
};

const deleteTask = async (req, res) => {
    try {
        const id = getTaskId(req);

        if (!id) {
            return res.status(400).json({
                success: false,
                message: "Invalid task ID",
            });
        }

        const task = await Task.findOne({
            where: {
                id,
                userId: req.user.id,
            },
        });

        if (!task) {
            return res.status(404).json({
                success: false,
                message: "Task not found",
            });
        }

        await task.destroy();

        return res.status(200).json({
            success: true,
            message: "Task deleted successfully",
        });
    } catch (error) {
        console.error("Delete task error:", error);

        return res.status(500).json({
            success: false,
            message: "Failed to delete task",
        });
    }
};

module.exports = {
    listTasks,
    getTask,
    createTask,
    updateTask,
    deleteTask,
};
