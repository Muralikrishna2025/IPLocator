package uk.ac.tees.mad.iplocator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.iplocator.model.dataclass.AuthResult
import uk.ac.tees.mad.iplocator.model.dataclass.UserDetails
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository
import uk.ac.tees.mad.iplocator.model.repository.SearchHistoryRepository

class ProfileScreenViewModel(private val authRepository: AuthRepository,
                             private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    private val _userDetails = MutableStateFlow<AuthResult<UserDetails>>(AuthResult.Loading)
    val userDetails: StateFlow<AuthResult<UserDetails>> = _userDetails

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        authRepository.getCurrentUserDetails()
            .onEach { result ->
                _userDetails.value = result
                _userId.value = authRepository.getCurrentUserId()
            }
            .launchIn(viewModelScope)
    }

    fun LogOut(){
        authRepository.LogOut()
    }

    fun clearUserSearchHistory(userId:String){
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistoryForUser(userId)
        }
    }
}