package uk.ac.tees.mad.iplocator.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository
import kotlin.Unit

class SignUpScreenViewModel(private val authRepository: AuthRepository) : ViewModel(){
    private val _signUpResult = MutableStateFlow<AuthResult<Boolean>>(AuthResult.Success(false))
    val signUpResult: StateFlow<AuthResult<Boolean>> = _signUpResult



    fun signUp(email: String, pass: String) {
        authRepository.signUp(email, pass).onEach { result ->
            _signUpResult.value = result
        }.launchIn(viewModelScope)
    }

}

