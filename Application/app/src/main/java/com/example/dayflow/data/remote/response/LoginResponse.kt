package com.example.dayflow.data.remote.response



data class LoginResponse(
    val message: String,
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String? = null
)