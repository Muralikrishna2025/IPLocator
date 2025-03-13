package uk.ac.tees.mad.iplocator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.iplocator.model.dataclass.LoadingState
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository

class SplashScreenViewModel(
    private val networkRepository: NetworkRepository, private val authRepository: AuthRepository
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState<Any>>(LoadingState.Loading)
    val loadingState: StateFlow<LoadingState<Any>> = _loadingState.asStateFlow()

    val isNetworkAvailable: Flow<Boolean> = networkRepository.isNetworkAvailable

    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            networkRepository.isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) {
                    println("Internet is available")
                } else {
                    println("Internet is not available")
                }
            }
        }
    }

    init {
        startLoading()
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun startLoading() {
        viewModelScope.launch {
            networkRepository.isNetworkAvailable.collectLatest { isAvailable ->
                if (isAvailable) {
                    // Simulate some data loading or initialization
                    // Replace this with your actual data loading logic
                    _loadingState.value = LoadingState.Loading
                    try {
                        delay(3000)
                        // Simulate a successful operation
                        // For example, fetching data from a network API
                        // val data = fetchDataFromApi()
                        // _loadingState.value = LoadingState.Success(data)
                        _loadingState.value = LoadingState.Success(Any())
                    } catch (e: Exception) {
                        _loadingState.value =
                            LoadingState.Error("Failed to load data: ${e.message}")
                    }
                } else {
                    delay(3000)
                    _loadingState.value = LoadingState.Error("No internet connection")
                }
            }
        }
    }
}