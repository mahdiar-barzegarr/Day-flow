const express = require("express");

const router = express.Router();

const validate = require("../../middlewares/validate");

const { registerSchema, loginSchema } = require("./auth.validation");

const { register, login, me } = require("./auth.controller");

const authenticate = require("../../middlewares/auth");

router.post("/register", validate(registerSchema), register);
router.post("/login", validate(loginSchema), login);
router.get("/me", authenticate, me);

module.exports = router;
