package com.example.loginproject.data.service

import com.example.dayflow.data.local.Token.TokenManager
import com.example.dayflow.data.remote.api.ApiService
import com.example.dayflow.data.remote.request.RefreshRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider


class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiServiceProvider: Provider<ApiService>
) : Authenticator {

    companion object {
        private val refreshMutex = Mutex()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath
        if (path.endsWith("/auth/refresh") || path.endsWith("/auth/login") ||
            path.endsWith("/auth/register") || path.endsWith("/auth/logout")) return null

        if (responseCount(response) >= 2) return null

        return runBlocking {
            refreshMutex.withLock {
                val failedAccessToken = response.request.header("Authorization")
                    ?.removePrefix("Bearer ")

                val currentAccess = tokenManager.getAccessTokenBlocking()
                if (currentAccess != null && currentAccess != failedAccessToken) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccess")
                        .build()
                }

                val refreshToken = tokenManager.getRefreshTokenBlocking()
                    ?: return@withLock null

                try {
                    val refreshResponse = apiServiceProvider.get()
                        .refresh(RefreshRequest(refreshToken))

                    if (refreshResponse.isSuccessful) {
                        val body = refreshResponse.body() ?: return@withLock null
                        tokenManager.updateAccessTokenBlocking(body.access)
                        tokenManager.updateRefreshTokenBlocking(body.refresh)
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${body.access}")
                            .build()
                    } else {
                        if (refreshResponse.code() in listOf(400, 401, 403)) tokenManager.clearSession()
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
