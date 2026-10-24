const express = require("express");

const router = express.Router();

const validate = require("../../middlewares/validate");

const {
    registerSchema,
    loginSchema,
    refreshSchema,
} = require("./auth.validation");

const { register, login, me, refresh } = require("./auth.controller");

const authenticate = require("../../middlewares/auth");

router.post("/register", validate(registerSchema), register);
router.post("/login", validate(loginSchema), login);
router.post("/refresh", validate(refreshSchema), refresh);
router.post("/logout", validate(refreshSchema), refresh);
router.get("/me", authenticate, me);

module.exports = router;
