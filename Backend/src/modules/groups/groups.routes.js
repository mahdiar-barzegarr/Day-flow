const express = require("express");

const router = express.Router();

const authenticate = require("../../middlewares/auth");
const validate = require("../../middlewares/validate");

const { createGroupSchema, updateGroupSchema } = require("./groups.validation");

const {
    listGroups,
    getGroup,
    createGroup,
    updateGroup,
    deleteGroup,
} = require("./groups.controller");

// All group endpoints require authentication.
router.use(authenticate);

router.get("/", listGroups);
router.get("/:id", getGroup);

router.post("/", validate(createGroupSchema), createGroup);

router.patch("/:id", validate(updateGroupSchema), updateGroup);

router.delete("/:id", deleteGroup);

module.exports = router;
