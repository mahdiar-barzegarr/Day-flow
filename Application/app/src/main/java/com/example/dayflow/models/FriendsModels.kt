package com.example.dayflow.models

enum class FriendStatus { NONE, REQUEST_SENT, FRIEND }

data class UserResult(
    val id: String,
    val name: String,
    val username: String,
    val mutualGroups: Int,
    val status: FriendStatus
)

data class FriendRequest(
    val id: String,
    val name: String,
    val username: String,
    val mutualInfo: String
)

data class FriendsUiState(
    val isLoggedIn: Boolean = false,
    val selectedTab: Int = 2, // 0 دوستان، 1 درخواست‌ها، 2 افزودن
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<UserResult> = emptyList(),
    val requests: List<FriendRequest> = emptyList(),
    val friendsCount: Int = 0
)
