/**
 * @swagger
 * tags:
 *   - name: Auth
 *     description: Authentication and user account endpoints
 *
 * components:
 *   schemas:
 *     RegisterRequest:
 *       type: object
 *       required:
 *         - username
 *         - email
 *         - password
 *       properties:
 *         username:
 *           type: string
 *           example: amir_kh
 *         email:
 *           type: string
 *           format: email
 *           example: amir@example.com
 *         password:
 *           type: string
 *           format: password
 *           minLength: 8
 *
 *     LoginRequest:
 *       type: object
 *       required:
 *         - email
 *         - password
 *       properties:
 *         email:
 *           type: string
 *           format: email
 *           example: amir@example.com
 *         password:
 *           type: string
 *           format: password
 *
 *     AuthResponse:
 *       type: object
 *       properties:
 *         success:
 *           type: boolean
 *           example: true
 *         accessToken:
 *           type: string
 *           example: eyJhbGciOiJIUzI1NiIs...
 */

/**
 * @swagger
 * /api/auth/register:
 *   post:
 *     tags:
 *       - Auth
 *     summary: Register a new user
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/RegisterRequest'
 *     responses:
 *       201:
 *         description: User registered successfully
 *       400:
 *         description: Validation error
 *       409:
 *         description: User already exists
 */

/**
 * @swagger
 * /api/auth/login:
 *   post:
 *     tags:
 *       - Auth
 *     summary: Log in
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/LoginRequest'
 *     responses:
 *       200:
 *         description: Login successful
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/AuthResponse'
 *       401:
 *         description: Invalid credentials
 */

/**
 * @swagger
 * /api/auth/me:
 *   get:
 *     tags:
 *       - Auth
 *     summary: Get the current user
 *     security:
 *       - BearerAuth: []
 *     responses:
 *       200:
 *         description: User profile returned successfully
 *       401:
 *         description: Missing or invalid access token
 */
