package com.example.dayflow.ui.friends


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.models.FriendRequest
import com.example.dayflow.models.FriendStatus
import com.example.dayflow.models.FriendsUiState
import com.example.dayflow.models.UserResult
import com.example.dayflow.repository.FriendsRepository
import com.example.dayflow.repository.SessionManager
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class FriendsViewModel(
    private val repo: FriendsRepository,
    session: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(FriendsUiState())
    val state: StateFlow<FriendsUiState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            session.isLoggedIn.collect { logged ->
                _state.update { it.copy(isLoggedIn = logged) }
                if (logged) loadRequests()
            }
        }
        // جستجو با ۴۰۰ میلی‌ثانیه تأخیر تا برای هر حرف درخواست نرود
        viewModelScope.launch {
            queryFlow.debounce(400).distinctUntilChanged().collect { q ->
                val clean = q.trim().removePrefix("@")
                if (clean.length < 2) {
                    _state.update { it.copy(results = emptyList(), isSearching = false) }
                } else {
                    _state.update { it.copy(isSearching = true) }
                    val res = repo.searchUsers(clean).getOrDefault(emptyList())
                    _state.update { it.copy(results = res, isSearching = false) }
                }
            }
        }
    }

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        queryFlow.value = q
    }

    fun onTabChange(tab: Int) = _state.update { it.copy(selectedTab = tab) }

    fun sendRequest(user: UserResult) = viewModelScope.launch {
        if (repo.sendRequest(user.id).isSuccess) {
            _state.update { s ->
                s.copy(results = s.results.map {
                    if (it.id == user.id) it.copy(status = FriendStatus.REQUEST_SENT) else it
                })
            }
        }
    }

    fun respond(req: FriendRequest, accept: Boolean) = viewModelScope.launch {
        if (repo.respond(req.id, accept).isSuccess) {
            _state.update { s ->
                s.copy(
                    requests = s.requests - req,
                    friendsCount = if (accept) s.friendsCount + 1 else s.friendsCount
                )
            }
        }
    }

    private fun loadRequests() = viewModelScope.launch {
        repo.getRequests().onSuccess { list -> _state.update { it.copy(requests = list) } }
    }
}