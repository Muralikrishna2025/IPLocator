package uk.ac.tees.mad.iplocator.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creative.ipfyandroid.Ipfy
import com.creative.ipfyandroid.IpfyClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.model.dataclass.LoadingState
import uk.ac.tees.mad.iplocator.model.repository.IpApiRepository
import uk.ac.tees.mad.iplocator.model.repository.IpstackRepository
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository
import java.io.IOException
import java.net.SocketTimeoutException

class HomeScreenViewModel(private val networkRepository: NetworkRepository, private val ipstackRepository: IpstackRepository, private val ipApiRepository: IpApiRepository): ViewModel() {


    sealed class ErrorState {
        /**
         * Indicates a network-related error (e.g., no internet connection).
         */
        object NetworkError : ErrorState()

        /**
         * Indicates a timeout error (e.g., the server took too long to respond).
         */
        object TimeoutError : ErrorState()

        /**
         * Indicates an error with the data itself (e.g., parsing error).
         */
        object DataError : ErrorState()

        /**
         * Indicates an unknown or unexpected error.
         */
        object UnknownError : ErrorState()
    }

    sealed class IpDetailsUiState {
        object Loading :  IpDetailsUiState()
        data class Success(val ipLocationDetails: IpLocation) :  IpDetailsUiState()
        data class Error(val errorState: ErrorState) :  IpDetailsUiState()
    }

    private val _ipDetailsUiState = MutableStateFlow<IpDetailsUiState>(IpDetailsUiState.Loading)
    val ipDetailsUiState: StateFlow<IpDetailsUiState> = _ipDetailsUiState.asStateFlow()

    private val _errorState = MutableStateFlow<ErrorState>(ErrorState.UnknownError)
    val errorState: StateFlow<ErrorState> = _errorState.asStateFlow()


    private val _deviceIp: MutableStateFlow<String?> = MutableStateFlow(null)
    val deviceIp= _deviceIp.asStateFlow()

    private val _ipLocation: MutableStateFlow<IpLocation?> = MutableStateFlow(null)
    val ipLocation: StateFlow<IpLocation?> = _ipLocation.asStateFlow()

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

    fun getDeviceIP() {
//        viewModelScope.launch {
//            val ipAddress = networkRepository.getDeviceIpAddress()
//            if(ipAddress!=null){
//            _deviceIp.update {
//                ipAddress
//            }
//            }
//        }

        viewModelScope.launch {
            _ipDetailsUiState.value = IpDetailsUiState.Loading
            ipApiRepository.getIpAddress().onSuccess { fetchedIp ->
                _errorState.value = ErrorState.UnknownError
                _deviceIp.value = fetchedIp
                //getIpLocationDetails(fetchedIp)
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
                _errorState.value = errorState
                _ipDetailsUiState.value = IpDetailsUiState.Error(errorState)

            }
        }
    }


    init {
        getDeviceIP()
    }

    fun getIpLocationDetails(ip: String) {
        viewModelScope.launch {
            _ipDetailsUiState.value = IpDetailsUiState.Loading
            ipstackRepository.getIpLocationDetails(ip).onSuccess { fetchedIpLocationDetails ->
                _errorState.value = ErrorState.UnknownError
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
                _errorState.value = errorState
                _ipDetailsUiState.value = IpDetailsUiState.Error(errorState)

            }
        }

    }

}