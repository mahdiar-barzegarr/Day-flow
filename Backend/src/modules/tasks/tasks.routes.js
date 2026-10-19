const express = require("express");

const router = express.Router();

const authenticate = require("../../middlewares/auth");
const validate = require("../../middlewares/validate");

const { createTaskSchema, updateTaskSchema } = require("./tasks.validation");

const {
    listTasks,
    getTask,
    createTask,
    updateTask,
    deleteTask,
} = require("./tasks.controller");

router.use(authenticate);

router.get("/", listTasks);
router.get("/:id", getTask);

router.post("/", validate(createTaskSchema), createTask);

router.patch("/:id", validate(updateTaskSchema), updateTask);

router.delete("/:id", deleteTask);

module.exports = router;
