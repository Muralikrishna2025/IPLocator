package uk.ac.tees.mad.iplocator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository

class LoginScreenViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _logInResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val logInResult: StateFlow<AuthResult<Boolean>> = _logInResult.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val _isLoginMode = MutableStateFlow(true)
    val isLoginMode = _isLoginMode.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun switchLoginMode() {
        _isLoginMode.value = !_isLoginMode.value
    }

    fun logIn(email: String, pass: String) {
        authRepository.logIn(email, pass).onEach { result ->
            _logInResult.value = result
        }.launchIn(viewModelScope)
    }
}