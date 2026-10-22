const { z } = require("zod");

const createTaskSchema = z.object({
    title: z
        .string()
        .trim()
        .min(1, "Task title is required")
        .max(200, "Task title must be at most 200 characters"),

    description: z
        .string()
        .trim()
        .max(5000, "Description must be at most 5000 characters")
        .nullable()
        .optional(),

    groupId: z.number().int().positive().nullable().optional(),

    status: z.enum(["pending", "in_progress", "completed"]).optional(),

    priority: z.enum(["low", "medium", "high"]).optional(),

    dueDate: z.string().datetime({ offset: true }).nullable().optional(),
});

const updateTaskSchema = z
    .object({
        title: z
            .string()
            .trim()
            .min(1, "Task title is required")
            .max(200, "Task title must be at most 200 characters")
            .optional(),

        description: z
            .string()
            .trim()
            .max(5000, "Description must be at most 5000 characters")
            .nullable()
            .optional(),

        groupId: z.number().int().positive().nullable().optional(),

        status: z.enum(["pending", "in_progress", "completed"]).optional(),

        priority: z.enum(["low", "medium", "high"]).optional(),

        dueDate: z.string().datetime({ offset: true }).nullable().optional(),
    })
    .refine((data) => Object.keys(data).length > 0, {
        message: "At least one field must be provided",
    });

module.exports = {
    createTaskSchema,
    updateTaskSchema,
};
