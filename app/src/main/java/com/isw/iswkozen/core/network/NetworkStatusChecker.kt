package com.isw.iswkozen.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isw.iswkozen.core.utilities.DeviceUtils

object NetworkStatusChecker {

    var networkConnectionState = false

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            networkConnectionState = true
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            networkConnectionState = false
        }
    }


    private fun checkNetwork(context: Context): Boolean {
        println("checking network status")
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        // should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }


    fun isConnectedToInternet(context: Context) = checkNetwork(context)

    fun checkNetworkNew(context: Context){

        val connectivityManager = getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
        try {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            connectivityManager.requestNetwork(networkRequest, networkCallback)
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
    }
}