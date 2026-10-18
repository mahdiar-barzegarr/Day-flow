/**
 * @swagger
 * tags:
 *   - name: Groups
 *     description: Personal groups owned by the authenticated user
 */

/**
 * @swagger
 * components:
 *   schemas:
 *     Group:
 *       type: object
 *       properties:
 *         id:
 *           type: integer
 *           example: 1
 *         userId:
 *           type: integer
 *           example: 7
 *         name:
 *           type: string
 *           maxLength: 100
 *           example: Work
 *         color:
 *           type: string
 *           pattern: '^#[0-9A-Fa-f]{6}$'
 *           example: '#3B82F6'
 *         icon:
 *           type: string
 *           maxLength: 50
 *           example: folder
 *         createdAt:
 *           type: string
 *           format: date-time
 *         updatedAt:
 *           type: string
 *           format: date-time
 *
 *     CreateGroupInput:
 *       type: object
 *       required:
 *         - name
 *       properties:
 *         name:
 *           type: string
 *           minLength: 1
 *           maxLength: 100
 *           example: Work
 *         color:
 *           type: string
 *           pattern: '^#[0-9A-Fa-f]{6}$'
 *           example: '#3B82F6'
 *         icon:
 *           type: string
 *           maxLength: 50
 *           example: folder
 *
 *     UpdateGroupInput:
 *       type: object
 *       minProperties: 1
 *       properties:
 *         name:
 *           type: string
 *           minLength: 1
 *           maxLength: 100
 *         color:
 *           type: string
 *           pattern: '^#[0-9A-Fa-f]{6}$'
 *         icon:
 *           type: string
 *           maxLength: 50
 *
 *     GroupResponse:
 *       type: object
 *       properties:
 *         success:
 *           type: boolean
 *           example: true
 *         data:
 *           $ref: '#/components/schemas/Group'
 *
 *     GroupsResponse:
 *       type: object
 *       properties:
 *         success:
 *           type: boolean
 *           example: true
 *         data:
 *           type: array
 *           items:
 *             $ref: '#/components/schemas/Group'
 */

/**
 * @swagger
 * /api/groups:
 *   get:
 *     summary: List the current user's groups
 *     tags: [Groups]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Groups retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/GroupsResponse'
 *       401:
 *         description: Unauthorized
 *
 *   post:
 *     summary: Create a group
 *     tags: [Groups]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateGroupInput'
 *     responses:
 *       201:
 *         description: Group created successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/GroupResponse'
 *       400:
 *         description: Validation error
 *       401:
 *         description: Unauthorized
 */

/**
 * @swagger
 * /api/groups/{id}:
 *   parameters:
 *     - in: path
 *       name: id
 *       required: true
 *       schema:
 *         type: integer
 *
 *   get:
 *     summary: Get a group by ID
 *     tags: [Groups]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Group retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/GroupResponse'
 *       401:
 *         description: Unauthorized
 *       404:
 *         description: Group not found
 *
 *   patch:
 *     summary: Update a group
 *     tags: [Groups]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateGroupInput'
 *     responses:
 *       200:
 *         description: Group updated successfully
 *       400:
 *         description: Validation error
 *       401:
 *         description: Unauthorized
 *       404:
 *         description: Group not found
 *
 *   delete:
 *     summary: Delete a group
 *     tags: [Groups]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Group deleted successfully
 *       401:
 *         description: Unauthorized
 *       404:
 *         description: Group not found
 */
