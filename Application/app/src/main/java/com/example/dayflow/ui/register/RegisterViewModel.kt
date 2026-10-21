package com.example.dayflow.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayflow.data.local.Token.TokenManager
import com.example.dayflow.data.remote.request.RegisterRequest
import com.example.dayflow.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UsernameStatus { Idle, Checking, Available, Taken }

/** امتیاز قدرت رمز: ۰ تا ۴ */
fun passwordStrength(p: String): Int {
    if (p.isEmpty()) return 0
    var score = 0
    if (p.length >= 8) score++
    if (p.any { it.isDigit() }) score++
    if (p.any { it.isLetter() } && p.any { it.isUpperCase() } && p.any { it.isLowerCase() }) score++
    if (p.any { !it.isLetterOrDigit() }) score++
    return score.coerceAtLeast(1)
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _usernameStatus = MutableStateFlow(UsernameStatus.Idle)
    val usernameStatus: StateFlow<UsernameStatus> = _usernameStatus

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible

    private val _acceptTerms = MutableStateFlow(false)
    val acceptTerms: StateFlow<Boolean> = _acceptTerms

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isRegisterSuccess = MutableStateFlow(false)
    val isRegisterSuccess: StateFlow<Boolean> = _isRegisterSuccess

    private var usernameJob: Job? = null

    fun onFullNameChanged(value: String) {
        _fullName.value = value
        _errorMessage.value = null
    }

    fun onUsernameChanged(value: String) {
        val clean = value.lowercase().filter { it.isLetterOrDigit() || it == '_' || it == '.' }
        _username.value = clean
        _errorMessage.value = null
        usernameJob?.cancel()
        if (clean.length < 3) {
            _usernameStatus.value = UsernameStatus.Idle
            return
        }
        usernameJob = viewModelScope.launch {
            _usernameStatus.value = UsernameStatus.Checking
            delay(500) // debounce
            // TODO: اینجا API چک یکتا بودن نام کاربری رو صدا بزن
            _usernameStatus.value = UsernameStatus.Available
        }
    }

    fun onEmailChanged(value: String) {
        _email.value = value.trim()
        _errorMessage.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun onConfirmPasswordChanged(value: String) {
        _confirmPassword.value = value
        _errorMessage.value = null
    }

    fun onTogglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun onAcceptTermsChanged(value: Boolean) {
        _acceptTerms.value = value
        _errorMessage.value = null
    }

    fun onCreateAccountClick() {
        val validationError = validate()
        if (validationError != null) {
            _errorMessage.value = validationError
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // RegisterRequest باید فیلدهای username و email رو داشته باشه
                val response = apiRepository.register(
                    RegisterRequest(
                        fullName = _fullName.value.trim(),
                        username = _username.value,
                        email = _email.value,
                        password = _password.value
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.access != null && body.refresh != null) {
                        tokenManager.saveSession(
                            accessToken = body.access,
                            refreshToken = body.refresh,
                            phoneNumber = _username.value
                        )
                        body.data?.full_name?.let { tokenManager.saveFullName(it) }
                    }
                    _isRegisterSuccess.value = true
                } else {
                    _errorMessage.value = when (response.code()) {
                        409 -> "این نام کاربری یا ایمیل قبلاً ثبت شده."
                        else -> "ثبت‌نام انجام نشد (کد ${response.code()})"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "اتصال برقرار نشد. اینترنت یا آدرس سرور را چک کن."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validate(): String? = when {
        _fullName.value.isBlank() -> "لطفاً نام نمایشی را وارد کن."
        _username.value.length < 3 -> "نام کاربری باید حداقل ۳ نویسه باشد."
        _usernameStatus.value == UsernameStatus.Taken -> "این نام کاربری قبلاً گرفته شده."
        !Patterns.EMAIL_ADDRESS.matcher(_email.value).matches() -> "ایمیل معتبر نیست."
        _password.value.length < 8 -> "رمز عبور باید حداقل ۸ نویسه باشد."
        _password.value != _confirmPassword.value -> "تکرار رمز عبور با رمز بالا یکسان نیست."
        !_acceptTerms.value -> "برای ادامه باید شرایط استفاده را بپذیری."
        else -> null
    }
}