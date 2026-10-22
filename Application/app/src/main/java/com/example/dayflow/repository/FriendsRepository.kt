package com.example.dayflow.repository

import com.example.dayflow.models.FriendRequest
import com.example.dayflow.models.UserResult
import com.example.dayflow.ui.friends.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val isLoggedIn: StateFlow<Boolean>
}

class FakeSessionManager : SessionManager {
    override val isLoggedIn = MutableStateFlow(false) // برای تست true کن
}

interface FriendsRepository {
    suspend fun searchUsers(query: String): Result<List<UserResult>>
    suspend fun sendRequest(userId: String): Result<Unit>
    suspend fun getRequests(): Result<List<FriendRequest>>
    suspend fun respond(requestId: String, accept: Boolean): Result<Unit>
}

class FakeFriendsRepository : FriendsRepository {
    override suspend fun searchUsers(query: String) = Result.success(emptyList<UserResult>())
    override suspend fun sendRequest(userId: String) = Result.success(Unit)
    override suspend fun getRequests() = Result.success(emptyList<FriendRequest>())
    override suspend fun respond(requestId: String, accept: Boolean) = Result.success(Unit)
}