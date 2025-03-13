package uk.ac.tees.mad.iplocator.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.iplocator.model.dataclass.ErrorState
import uk.ac.tees.mad.iplocator.model.dataclass.IpDetailsUiState
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.model.repository.IpstackRepository
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository
import java.io.IOException
import java.net.SocketTimeoutException

class SearchScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val ipstackRepository: IpstackRepository,
) : ViewModel() {
    private val _ipDetailsUiState = MutableStateFlow<IpDetailsUiState>(IpDetailsUiState.Loading)
    val ipDetailsUiState: StateFlow<IpDetailsUiState> = _ipDetailsUiState.asStateFlow()

    private val _ipLocation: MutableStateFlow<IpLocation?> = MutableStateFlow(null)
    val ipLocation: StateFlow<IpLocation?> = _ipLocation.asStateFlow()

    val isNetworkAvailable: Flow<Boolean> = networkRepository.isNetworkAvailable

    private val _isErrorInput = MutableStateFlow(false)
    val isErrorInput: StateFlow<Boolean> = _isErrorInput.asStateFlow()

    private val _inputIp = MutableStateFlow("")
    val inputIp: StateFlow<String> = _inputIp.asStateFlow()

    private val _searchBarExpanded = MutableStateFlow(false)
    val searchBarExpanded: StateFlow<Boolean> = _searchBarExpanded.asStateFlow()

    fun updateInputIp(newInputIp: String) {
        _inputIp.value = newInputIp
    }

    fun updateIsErrorInput(newIsErrorInput: Boolean) {
        _isErrorInput.value = newIsErrorInput
    }

    fun updateSearchBarExpanded(newSearchBarExpanded: Boolean) {
        _searchBarExpanded.value = newSearchBarExpanded
    }

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

    fun getIpLocationDetails(ip: String) {
        viewModelScope.launch {
            _ipDetailsUiState.value = IpDetailsUiState.Loading
            ipstackRepository.getIpLocationDetails(ip).onSuccess { fetchedIpLocationDetails ->
                _ipDetailsUiState.value = IpDetailsUiState.Success(fetchedIpLocationDetails)
                _ipLocation.value = fetchedIpLocationDetails
            }.onFailure { exception ->
                val errorState = when (exception) {
                    is SocketTimeoutException -> {
                        // `Log.e` is used to log errors to the console.
                        Log.e("myApp", "Connection timed out: ${exception.message}")
                        ErrorState.TimeoutError
                    }

                    is IOException -> {
                        Log.e("myApp", "No internet connection: ${exception.message}")
                        ErrorState.NetworkError
                    }

                    else -> {
                        Log.e("myApp", "Error fetching items: ${exception.message}")
                        ErrorState.UnknownError
                    }
                }
                _ipDetailsUiState.value =
                    IpDetailsUiState.Error(errorState, exception.message.toString())
            }
        }
    }
}