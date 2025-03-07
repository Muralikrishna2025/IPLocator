package uk.ac.tees.mad.iplocator.model.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.iplocator.ui.utils.NetworkConnectivityManager

class NetworkRepository(private val networkConnectivityManager: NetworkConnectivityManager) {
    val isNetworkAvailable: Flow<Boolean> = networkConnectivityManager.observeConnectivity()

    fun getDeviceIpAddress():String?{
        return networkConnectivityManager.getDeviceIpAddress()
    }
}