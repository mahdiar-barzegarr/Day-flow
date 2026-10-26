package com.example.dayflow.data.remote.api

import com.example.dayflow.data.remote.request.LogOutRequest
import com.example.dayflow.data.remote.request.LoginRequest
import com.example.dayflow.data.remote.request.RefreshRequest
import com.example.dayflow.data.remote.request.RegisterRequest
import com.example.dayflow.data.remote.response.LoginResponse
import com.example.dayflow.data.remote.response.ProfileResponse
import com.example.dayflow.data.remote.response.RefreshResponse
import com.example.dayflow.data.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
    @POST("refresh")
    suspend fun refresh(@Body body: RefreshRequest): Response<RefreshResponse>
    @POST("logout")
    suspend fun logout(@Body body: LogOutRequest): Response<Unit>
    @GET("me")
    suspend fun me(): Response<ProfileResponse>
}