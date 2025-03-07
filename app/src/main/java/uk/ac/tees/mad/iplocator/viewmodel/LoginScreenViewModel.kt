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

class LoginScreenViewModel(private val authRepository: AuthRepository) : ViewModel(){
    private val _logInResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val logInResult: StateFlow<AuthResult<Boolean>> = _logInResult.asStateFlow()

    fun logIn(email: String, pass: String) {
        authRepository.logIn(email, pass).onEach { result ->
            _logInResult.value = result
        }.launchIn(viewModelScope)
    }
}