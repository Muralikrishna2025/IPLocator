package uk.ac.tees.mad.iplocator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.model.dataclass.UserDetails
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository

class ProfileScreenViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _userDetails = MutableStateFlow<AuthResult<UserDetails>>(AuthResult.Loading)
    val userDetails: StateFlow<AuthResult<UserDetails>> = _userDetails

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        authRepository.getCurrentUserDetails()
            .onEach { result ->
                _userDetails.value = result
            }
            .launchIn(viewModelScope)
    }
}