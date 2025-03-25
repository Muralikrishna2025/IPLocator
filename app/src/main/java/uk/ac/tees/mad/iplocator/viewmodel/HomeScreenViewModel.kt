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
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocationData
import uk.ac.tees.mad.iplocator.model.repository.IpApiRepository
import uk.ac.tees.mad.iplocator.model.repository.IpLocationDataRepository
import uk.ac.tees.mad.iplocator.model.repository.IpstackRepository
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository
import java.io.IOException
import java.net.SocketTimeoutException

class HomeScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val ipstackRepository: IpstackRepository,
    private val ipApiRepository: IpApiRepository,
    private val ipLocationDataRepository: IpLocationDataRepository
) : ViewModel() {

    private val _ipDetailsUiState = MutableStateFlow<IpDetailsUiState>(IpDetailsUiState.Loading)
    val ipDetailsUiState: StateFlow<IpDetailsUiState> = _ipDetailsUiState.asStateFlow()

    private val _deviceIp: MutableStateFlow<String?> = MutableStateFlow(null)
    val deviceIp = _deviceIp.asStateFlow()

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
        viewModelScope.launch {
            _ipDetailsUiState.value = IpDetailsUiState.Loading
            ipApiRepository.getIpAddress().onSuccess { fetchedIp ->
                _deviceIp.value = fetchedIp
                getIpLocationDetails(fetchedIp)
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


    init {
        getDeviceIP()
    }

    fun getIpLocationDetails(ip: String) {
        viewModelScope.launch {
            _ipDetailsUiState.value = IpDetailsUiState.Loading
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
                _ipDetailsUiState.value = IpDetailsUiState.Success(fetchedIpLocationDetails)
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
                if (ipLocationDataRepository.countIpLocationDataByIp(ip) > 0) {
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
                    _ipDetailsUiState.value =
                        IpDetailsUiState.Error(errorState, exception.message.toString())
                }
            }
        }

    }

}