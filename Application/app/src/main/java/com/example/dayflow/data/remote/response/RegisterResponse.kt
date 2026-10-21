package com.example.dayflow.data.remote.response



data class RegisterResponse(
    val data: RegisteredUserData?,
    val access: String?,
    val refresh: String?
)

data class RegisteredUserData(
    val full_name: String?,
    val phone_number: String?
)