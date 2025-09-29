package com.example.persianaiapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.getSystemService
import com.example.persianaiapp.data.local.SettingsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages network connectivity status and offline mode
 */
@Singleton
class NetworkManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager
) {

    private val _isOnline = MutableStateFlow(false)
    private val _isMetered = MutableStateFlow(false)
    private val _networkType = MutableStateFlow<NetworkType>(NetworkType.UNKNOWN)
    
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    val isMetered: StateFlow<Boolean> = _isMetered.asStateFlow()
    val networkType: StateFlow<NetworkType> = _networkType.asStateFlow()
    
    private val connectivityManager by lazy {
        context.getSystemService<ConnectivityManager>()
    }
    
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            updateNetworkStatus()
        }
        
        override fun onLost(network: Network) {
            super.onLost(network)
            updateNetworkStatus()
        }
        
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            updateNetworkStatus()
        }
        
        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
            updateNetworkStatus()
        }
    }
    
    init {
        registerNetworkCallback()
    }
    
    /**
     * Register network callback to monitor network changes
     */
    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        
        connectivityManager?.registerNetworkCallback(
            networkRequest,
            networkCallback
        )
        
        // Initial update
        updateNetworkStatus()
    }
    
    /**
     * Update network status based on current connectivity
     */
    private fun updateNetworkStatus() {
        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        
        val isConnected = capabilities?.let { 
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && 
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } ?: false
        
        _isOnline.value = isConnected && !settingsManager.isOfflineMode()
        
        // Check if network is metered
        _isMetered.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            connectivityManager?.isActiveNetworkMetered ?: true
        } else {
            @Suppress("DEPRECATION")
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) != true
        }
        
        // Determine network type
        _networkType.value = when {
            !isConnected -> NetworkType.DISCONNECTED
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkType.WIFI
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkType.CELLULAR
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> NetworkType.ETHERNET
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true -> NetworkType.VPN
            else -> NetworkType.UNKNOWN
        }
    }
    
    /**
     * Check if the app should use online features
     */
    fun shouldUseOnlineFeatures(): Boolean {
        return _isOnline.value && !settingsManager.isOfflineMode()
    }
    
    /**
     * Toggle offline mode
     */
    fun setOfflineMode(enabled: Boolean) {
        settingsManager.setOfflineMode(enabled)
        updateNetworkStatus()
    }
    
    /**
     * Clean up network callbacks
     */
    fun unregister() {
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Ignore if callback was not registered
        }
    }
    
    /**
     * Network type enum
     */
    enum class NetworkType {
        WIFI,
        CELLULAR,
        ETHERNET,
        VPN,
        DISCONNECTED,
        UNKNOWN
    }
}
