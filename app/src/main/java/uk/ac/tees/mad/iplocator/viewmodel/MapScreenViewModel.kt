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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.iplocator.model.dataclass.ErrorState
import uk.ac.tees.mad.iplocator.model.dataclass.IpDetailsUiState
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocationData
import uk.ac.tees.mad.iplocator.model.repository.IpLocationDataRepository
import uk.ac.tees.mad.iplocator.model.repository.IpstackRepository
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.Locale

class MapScreenViewModel(
    private val ipstackRepository: IpstackRepository,
    private val ipLocationDataRepository: IpLocationDataRepository
) : ViewModel() {

    private val _ipDetailsUiState = MutableStateFlow<IpDetailsUiState>(IpDetailsUiState.Loading)
    val ipDetailsUiState: StateFlow<IpDetailsUiState> = _ipDetailsUiState.asStateFlow()

    private val _ipLocation: MutableStateFlow<IpLocation?> = MutableStateFlow(null)
    val ipLocation: StateFlow<IpLocation?> = _ipLocation.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: MutableStateFlow<Boolean> = _isRefreshing

    private val _IplocationData = MutableStateFlow(IpLocationData())
    val ipLocationData: StateFlow<IpLocationData> = _IplocationData.asStateFlow()

    private val _offlineMode = MutableStateFlow<Boolean?>(null)
    val offlineMode: StateFlow<Boolean?> = _offlineMode.asStateFlow()

    fun refreshLocation(ip: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                getIpLocationDetails(ip)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun getIpLocationDetails(ip: String) {
        viewModelScope.launch {
            _ipDetailsUiState.value = IpDetailsUiState.Loading
            _offlineMode.value = null
            ipstackRepository.getIpLocationDetails(ip).onSuccess { fetchedIpLocationDetails ->
                _ipLocation.value = fetchedIpLocationDetails
                ipLocationDataRepository.upsertIpLocationData(
                    IpLocationData(
                        ip = _ipLocation.value?.ip.toString(),
                        type = _ipLocation.value?.type.toString(),
                        continentCode = _ipLocation.value?.continentCode.toString(),
                        continentName = _ipLocation.value?.continentName.toString(),
                        countryCode = _ipLocation.value?.countryCode.toString(),
                        countryName = _ipLocation.value?.countryName.toString(),
                        regionCode = _ipLocation.value?.regionCode.toString(),
                        regionName = _ipLocation.value?.regionName.toString(),
                        city = _ipLocation.value?.city.toString(),
                        zip = _ipLocation.value?.zip.toString(),
                        latitude = _ipLocation.value?.latitude,
                        longitude = _ipLocation.value?.longitude,
                        msa = _ipLocation.value?.msa.toString(),
                        dma = _ipLocation.value?.dma.toString(),
                        radius = _ipLocation.value?.radius.toString(),
                        ipRoutingType = _ipLocation.value?.ipRoutingType.toString(),
                        connectionType = _ipLocation.value?.connectionType.toString(),
                        location = _ipLocation.value?.location,
                    )
                )
                _offlineMode.value = false
                _ipDetailsUiState.value = IpDetailsUiState.Success(fetchedIpLocationDetails)
            }.onFailure { exception ->
                val errorState = when (exception) {
                    is SocketTimeoutException -> {
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
                if (ipLocationDataRepository.countIpLocationDataByIp(ip) > 0) {
                    _offlineMode.value = true
                    val ipLocationDataFromDB = ipLocationDataRepository.getIpLocationDataByIp(ip)
                    if (ipLocationDataFromDB != null) {
                        _ipDetailsUiState.value = IpDetailsUiState.Success(
                            IpLocation(
                                ip = ipLocationDataFromDB.ip,
                                type = ipLocationDataFromDB.type,
                                continentCode = ipLocationDataFromDB.continentCode,
                                continentName = ipLocationDataFromDB.continentName,
                                countryCode = ipLocationDataFromDB.countryCode,
                                countryName = ipLocationDataFromDB.countryName,
                                regionCode = ipLocationDataFromDB.regionCode,
                                regionName = ipLocationDataFromDB.regionName,
                                city = ipLocationDataFromDB.city,
                                zip = ipLocationDataFromDB.zip,
                                latitude = ipLocationDataFromDB.latitude,
                                longitude = ipLocationDataFromDB.longitude,
                                msa = ipLocationDataFromDB.msa,
                                dma = ipLocationDataFromDB.dma,
                                radius = ipLocationDataFromDB.radius,
                                ipRoutingType = ipLocationDataFromDB.ipRoutingType,
                                connectionType = ipLocationDataFromDB.connectionType,
                                location = ipLocationDataFromDB.location
                            )
                        )
                        _ipLocation.value = IpLocation(
                            ip = ipLocationDataFromDB.ip,
                            type = ipLocationDataFromDB.type,
                            continentCode = ipLocationDataFromDB.continentCode,
                            continentName = ipLocationDataFromDB.continentName,
                            countryCode = ipLocationDataFromDB.countryCode,
                            countryName = ipLocationDataFromDB.countryName,
                            regionCode = ipLocationDataFromDB.regionCode,
                            regionName = ipLocationDataFromDB.regionName,
                            city = ipLocationDataFromDB.city,
                            zip = ipLocationDataFromDB.zip,
                            latitude = ipLocationDataFromDB.latitude,
                            longitude = ipLocationDataFromDB.longitude,
                            msa = ipLocationDataFromDB.msa,
                            dma = ipLocationDataFromDB.dma,
                            radius = ipLocationDataFromDB.radius,
                            ipRoutingType = ipLocationDataFromDB.ipRoutingType,
                            connectionType = ipLocationDataFromDB.connectionType,
                            location = ipLocationDataFromDB.location
                        )
                    }
                } else {
                    _offlineMode.value = true
                    _ipDetailsUiState.value =
                        IpDetailsUiState.Error(errorState, exception.message.toString())
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    fun reverseGeocodeLocation(context: Context, latitude: Double?, longitude: Double?): String {
        if (_offlineMode.value == true) {
            var address = "Address not available in Offline Mode"
            return address
        } else if (_offlineMode.value == false) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val coordinate = LatLng(latitude ?: 0.0, longitude ?: 0.0)
            val addresses: MutableList<Address>? =
                geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)
            return if (addresses != null && addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Address not found"
            }
        } else {
            var address = "Address not found"
            return address
        }
    }

    fun updateIpAndLocation(ip: String, latitude: Double?, longitude: Double?, context: Context) {
        viewModelScope.launch {
            val address = reverseGeocodeLocation(context, latitude, longitude)
            _IplocationData.update {
                it.copy(
                    ip = ip,
                    latitude = latitude,
                    longitude = longitude,
                    coordinates = LatLng(latitude ?: 0.0, longitude ?: 0.0),
                    address = address
                )
            }
            ipLocationDataRepository.updateIpLocation(
                ip = ip,
                latitude = latitude,
                longitude = longitude,
                coordinates = LatLng(latitude ?: 0.0, longitude ?: 0.0),
                address = address
            )
        }
    }

}