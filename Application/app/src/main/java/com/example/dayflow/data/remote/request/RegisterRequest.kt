package com.example.dayflow.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("user_name")
    val username : String,

    @SerializedName("email")
    val email: String,

    val password: String,


)