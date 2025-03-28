package uk.ac.tees.mad.iplocator.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.iplocator.model.dataclass.ErrorState
import uk.ac.tees.mad.iplocator.model.dataclass.IpDetailsUiState
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocationData
import uk.ac.tees.mad.iplocator.model.dataclass.SearchHistoryItem
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository
import uk.ac.tees.mad.iplocator.model.repository.IpLocationDataRepository
import uk.ac.tees.mad.iplocator.model.repository.IpstackRepository
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository
import uk.ac.tees.mad.iplocator.model.repository.SearchHistoryRepository
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.LocalDateTime

class SearchScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val ipstackRepository: IpstackRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val authRepository: AuthRepository,
    private val ipLocationDataRepository: IpLocationDataRepository
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

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<SearchHistoryItem>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistoryItem>> = _searchHistory.asStateFlow()

    private val _offlineMode = MutableStateFlow<Boolean?>(null)
    val offlineMode: StateFlow<Boolean?> = _offlineMode.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val userId = getCurrentUserId()
            if (userId != null) {
                _userId.value = userId
                _searchHistory.update {
                    searchHistoryRepository.getSearchHistoryForUser(userId)
                }
            }
        }
    }

    fun updateInputIp(newInputIp: String) {
        _inputIp.value = newInputIp
    }

    fun updateIsErrorInput(newIsErrorInput: Boolean) {
        _isErrorInput.value = newIsErrorInput
    }

    fun updateSearchBarExpanded(newSearchBarExpanded: Boolean) {
        _searchBarExpanded.value = newSearchBarExpanded
    }

    private fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
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
            _offlineMode.value = null
            ipstackRepository.getIpLocationDetails(ip).onSuccess { fetchedIpLocationDetails ->
                val userId = getCurrentUserId()
                if (userId != null) {
                    saveSearchQuery(userId, ip)
                    _searchHistory.update {
                        searchHistoryRepository.getSearchHistoryForUser(userId)
                    }
                }
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
                _offlineMode.value = true
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

    private fun saveSearchQuery(userId: String, query: String) {
        viewModelScope.launch {
            val currentTimestamp = LocalDateTime.now()
            // Try to update the timestamp first
            if (searchHistoryRepository.isQueryPresent(userId, query)) {
                searchHistoryRepository.updateTimestampForExistingQuery(
                    userId, query, currentTimestamp
                )
            } else {

                // If no rows were updated (meaning the query didn't exist), insert a new row
                val searchHistoryItem = SearchHistoryItem(
                    userId = userId, searchedQuery = query, timestamp = currentTimestamp
                )
                searchHistoryRepository.insertSearchHistory(searchHistoryItem)
            }
            _searchHistory.update {
                searchHistoryRepository.getSearchHistoryForUser(userId)
            }
        }
    }

    suspend fun getSearchHistory(userId: String) =
        searchHistoryRepository.getSearchHistoryForUser(userId)
}