package com.dyaco.spirit_commercial.support

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class NetworkHelper {
    companion object {
        @JvmStatic
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            val isConnected = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            if (!isConnected) return false

            return try {
                val url = URL("https://clients3.google.com/generate_204")
                (url.openConnection() as? HttpURLConnection)?.run {
                    setRequestProperty("User-Agent", "Android")
                    setRequestProperty("Connection", "close")
                    connectTimeout = 1500
                    connect()
                    responseCode == 204
                } ?: false
            } catch (e: IOException) {
                false
            }
        }
    }
}
