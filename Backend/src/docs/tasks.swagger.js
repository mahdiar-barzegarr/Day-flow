/**
 * @swagger
 * tags:
 *   - name: Tasks
 *     description: Task management for authenticated users
 */

/**
 * @swagger
 * components:
 *   schemas:
 *     Task:
 *       type: object
 *       properties:
 *         id:
 *           type: integer
 *           example: 1
 *         userId:
 *           type: integer
 *           example: 7
 *         groupId:
 *           type: integer
 *           nullable: true
 *           example: 2
 *         title:
 *           type: string
 *           example: Finish the project
 *         description:
 *           type: string
 *           nullable: true
 *           example: Complete the backend API
 *         status:
 *           type: string
 *           enum: [pending, in_progress, completed]
 *           example: pending
 *         priority:
 *           type: string
 *           enum: [low, medium, high]
 *           example: high
 *         dueDate:
 *           type: string
 *           format: date-time
 *           nullable: true
 *         createdAt:
 *           type: string
 *           format: date-time
 *         updatedAt:
 *           type: string
 *           format: date-time
 *
 *     CreateTaskInput:
 *       type: object
 *       required:
 *         - title
 *       properties:
 *         title:
 *           type: string
 *           maxLength: 200
 *           example: Finish the project
 *         description:
 *           type: string
 *           nullable: true
 *         groupId:
 *           type: integer
 *           nullable: true
 *         status:
 *           type: string
 *           enum: [pending, in_progress, completed]
 *         priority:
 *           type: string
 *           enum: [low, medium, high]
 *         dueDate:
 *           type: string
 *           format: date-time
 *           nullable: true
 *
 *     UpdateTaskInput:
 *       type: object
 *       minProperties: 1
 *       properties:
 *         title:
 *           type: string
 *           maxLength: 200
 *         description:
 *           type: string
 *           nullable: true
 *         groupId:
 *           type: integer
 *           nullable: true
 *         status:
 *           type: string
 *           enum: [pending, in_progress, completed]
 *         priority:
 *           type: string
 *           enum: [low, medium, high]
 *         dueDate:
 *           type: string
 *           format: date-time
 *           nullable: true
 */

/**
 * @swagger
 * /api/tasks:
 *   get:
 *     summary: Get the current user's tasks
 *     tags: [Tasks]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: query
 *         name: status
 *         schema:
 *           type: string
 *           enum: [pending, in_progress, completed]
 *       - in: query
 *         name: priority
 *         schema:
 *           type: string
 *           enum: [low, medium, high]
 *       - in: query
 *         name: groupId
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: Tasks retrieved successfully
 *       400:
 *         description: Invalid query parameter
 *       401:
 *         description: Unauthorized
 *
 *   post:
 *     summary: Create a task
 *     tags: [Tasks]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateTaskInput'
 *     responses:
 *       201:
 *         description: Task created successfully
 *       400:
 *         description: Validation error or invalid group
 *       401:
 *         description: Unauthorized
 *
 * /api/tasks/{id}:
 *   parameters:
 *     - in: path
 *       name: id
 *       required: true
 *       schema:
 *         type: integer
 *
 *   get:
 *     summary: Get a task by ID
 *     tags: [Tasks]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Task retrieved successfully
 *       400:
 *         description: Invalid task ID
 *       401:
 *         description: Unauthorized
 *       404:
 *         description: Task not found
 *
 *   patch:
 *     summary: Update a task
 *     tags: [Tasks]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateTaskInput'
 *     responses:
 *       200:
 *         description: Task updated successfully
 *       400:
 *         description: Validation error or invalid group
 *       401:
 *         description: Unauthorized
 *       404:
 *         description: Task not found
 *
 *   delete:
 *     summary: Delete a task
 *     tags: [Tasks]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Task deleted successfully
 *       400:
 *         description: Invalid task ID
 *       401:
 *         description: Unauthorized
 *       404:
 *         description: Task not found
 */
