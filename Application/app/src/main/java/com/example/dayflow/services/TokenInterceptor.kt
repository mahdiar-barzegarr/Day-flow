package com.example.loginproject.data.service

import com.example.dayflow.data.local.Token.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenManager.getAccessTokenBlocking()

        val request = if (!token.isNullOrEmpty()) {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}