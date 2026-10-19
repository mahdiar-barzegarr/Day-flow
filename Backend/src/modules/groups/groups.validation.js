const { z } = require("zod");

const createGroupSchema = z.object({
    name: z
        .string()
        .trim()
        .min(1, "Group name is required")
        .max(100, "Group name must be at most 100 characters"),

    color: z
        .string()
        .regex(
            /^#[0-9A-Fa-f]{6}$/,
            "Color must be a valid hex color, e.g. #3B82F6",
        )
        .optional(),

    icon: z
        .string()
        .trim()
        .min(1, "Icon cannot be empty")
        .max(50, "Icon must be at most 50 characters")
        .optional(),
});

const updateGroupSchema = z
    .object({
        name: z
            .string()
            .trim()
            .min(1, "Group name is required")
            .max(100, "Group name must be at most 100 characters")
            .optional(),

        color: z
            .string()
            .regex(
                /^#[0-9A-Fa-f]{6}$/,
                "Color must be a valid hex color, e.g. #3B82F6",
            )
            .optional(),

        icon: z
            .string()
            .trim()
            .min(1, "Icon cannot be empty")
            .max(50, "Icon must be at most 50 characters")
            .optional(),
    })
    .refine((data) => Object.keys(data).length > 0, {
        message: "At least one field must be provided",
    });

module.exports = {
    createGroupSchema,
    updateGroupSchema,
};
