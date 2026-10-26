package com.example.dayflow.data.remote.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @SerializedName("identifier")
    val identifier: String,

    val password: String

)
