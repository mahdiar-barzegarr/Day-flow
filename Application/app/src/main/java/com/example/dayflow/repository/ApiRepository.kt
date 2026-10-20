package com.example.dayflow.repository

import com.example.dayflow.data.remote.api.ApiService
import com.example.dayflow.data.remote.request.LoginRequest
import com.example.dayflow.data.remote.request.RefreshRequest
import com.example.dayflow.data.remote.request.RegisterRequest
import com.example.dayflow.data.remote.response.LoginResponse
import com.example.dayflow.data.remote.response.ProfileResponse
import com.example.dayflow.data.remote.response.RefreshResponse
import com.example.dayflow.data.remote.response.RegisterResponse
import jakarta.inject.Inject
import retrofit2.Response

class ApiRepository @Inject constructor(

    private val api: ApiService

) {

    suspend fun register(

        request: RegisterRequest

    ): Response<RegisterResponse> {

        return api.register(request)

    }


    suspend fun login(

        request: LoginRequest

    ): Response<LoginResponse> {

        return api.login(request)

    }


    suspend fun getProfile(): Response<ProfileResponse> = api.getProfile()


    suspend fun refreshToken(refresh: String): Response<RefreshResponse> =
        api.refreshToken(RefreshRequest(refresh))


}
