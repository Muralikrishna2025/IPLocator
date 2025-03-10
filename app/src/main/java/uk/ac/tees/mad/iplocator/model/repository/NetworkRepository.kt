package uk.ac.tees.mad.iplocator.model.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.iplocator.ui.utils.NetworkConnectivityManager

class NetworkRepository(private val networkConnectivityManager: NetworkConnectivityManager) {
    val isNetworkAvailable: Flow<Boolean> = networkConnectivityManager.observeConnectivity()

    // Gives the device's Local IP address which we don't need
    fun getDeviceIpAddress(): String? {
        return networkConnectivityManager.getDeviceIpAddress()
    }
}