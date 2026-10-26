package com.example.dayflow.data.remote.response

data class RefreshResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String
)
