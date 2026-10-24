const { z } = require("zod");

const registerSchema = z.object({
    username: z
        .string()
        .trim()
        .min(3, "Username must be at least 3 characters")
        .max(30, "Username must be at most 30 characters")
        .regex(
            /^[a-zA-Z0-9_]+$/,
            "Username can only contain letters, numbers and underscores",
        ),

    email: z
        .string()
        .trim()
        .email()
        .max(254)
        .transform((value) => value.toLowerCase()),

    password: z
        .string()
        .min(8, "Password must be at least 8 characters")
        .max(128, "Password must be at most 128 characters"),
});

const loginSchema = z.object({
    identifier: z.string().trim().min(1, "Email or username is required"),
    password: z.string().min(1, "Password is required").max(128),
});

const refreshSchema = z.object({
    refreshToken: z.string().min(1),
});

module.exports = {
    registerSchema,
    loginSchema,
    refreshSchema,
};
