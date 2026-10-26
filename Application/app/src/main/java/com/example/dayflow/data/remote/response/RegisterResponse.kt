package com.example.dayflow.data.remote.response



data class RegisterResponse(
    val message: String,
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String? = null
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: String
)