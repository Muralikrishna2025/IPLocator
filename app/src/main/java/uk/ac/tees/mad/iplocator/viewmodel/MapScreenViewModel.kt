package uk.ac.tees.mad.iplocator.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.iplocator.model.dataclass.ErrorState
import uk.ac.tees.mad.iplocator.model.dataclass.IpDetailsUiState
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.model.repository.IpstackRepository
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Locale

class MapScreenViewModel(
    private val ipstackRepository: IpstackRepository
) : ViewModel() {

    private val _ipDetailsUiState = MutableStateFlow<IpDetailsUiState>(IpDetailsUiState.Loading)
    val ipDetailsUiState: StateFlow<IpDetailsUiState> = _ipDetailsUiState.asStateFlow()

    private val _ipLocation: MutableStateFlow<IpLocation?> = MutableStateFlow(null)
    val ipLocation: StateFlow<IpLocation?> = _ipLocation.asStateFlow()

    // Add these variables to handle the refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _latitude = MutableStateFlow<Double?>(null)
    val latitude: StateFlow<Double?> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow<Double?>(null)
    val longitude: StateFlow<Double?> = _longitude.asStateFlow()

    private val _ip = MutableStateFlow<String?>(null)
    val ip: StateFlow<String?> = _ip.asStateFlow()

    private val _address = MutableStateFlow<String?>(null)
    val address: StateFlow<String?> = _address.asStateFlow()

    private val _coordinates = MutableStateFlow<LatLng>(
        LatLng(
            latitude.value ?: 0.0, longitude.value ?: 0.0
        )
    )
    val coordinates: StateFlow<LatLng> = _coordinates.asStateFlow()


    // Add this function to handle the refresh
    fun refreshLocation(ip: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // Here you'll make your API call to ipstack
                // val newLocation = repository.getFreshLocation(ip)
                // Update your state with new coordinates
                getIpLocationDetails(ip)
            } finally {
                _isRefreshing.value = false
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

    fun reverseGeocodeLocation(context: Context, latitude: Double?, longitude: Double?): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(latitude!!, longitude!!)
        val addresses: MutableList<Address>? =
            geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)
        return if (addresses != null && addresses.isNotEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            "Address not found"
        }
    }

    fun updateIpAndLocation(ip: String, latitude: Double?, longitude: Double?, context: Context) {
        viewModelScope.launch {
            _ip.value = ip
            _latitude.value = latitude
            _longitude.value = longitude
            _coordinates.value = LatLng(latitude!!, longitude!!)
            _address.value = reverseGeocodeLocation(context = context, latitude, longitude)
        }
    }

}