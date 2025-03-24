package uk.ac.tees.mad.iplocator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.model.dataclass.UserData
import uk.ac.tees.mad.iplocator.model.dataclass.UserDetails
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository
import uk.ac.tees.mad.iplocator.model.repository.SearchHistoryRepository

class ProfileScreenViewModel(
    private val authRepository: AuthRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    private val _userDetails = MutableStateFlow<AuthResult<UserDetails>>(AuthResult.Loading)
    val userDetails: StateFlow<AuthResult<UserDetails>> = _userDetails.asStateFlow()

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        viewModelScope.launch {
            authRepository.getCurrentUserDetails().collect { result ->
                _userDetails.value = result
                if (result is AuthResult.Success) {
                    _userData.update {
                        it.copy(
                            userDetails = result.data, userId = authRepository.getCurrentUserId()
                        )
                    }
                }
            }
        }
    }

    fun logOut() {
        authRepository.LogOut()
    }

    fun clearUserSearchHistory(userId: String) {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistoryForUser(userId)
        }
    }
}