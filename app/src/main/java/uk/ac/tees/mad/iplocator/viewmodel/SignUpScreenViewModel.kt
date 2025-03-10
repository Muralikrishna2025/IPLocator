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

class SignUpScreenViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _signUpResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val signUpResult: StateFlow<AuthResult<Boolean>> = _signUpResult.asStateFlow()

    fun signUp(email: String, pass: String) {
        authRepository.signUp(email, pass).onEach { result ->
            _signUpResult.value = result
        }.launchIn(viewModelScope)
    }

}

