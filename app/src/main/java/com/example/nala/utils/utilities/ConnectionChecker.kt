package com.example.nala.utils.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionChecker @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun isNetworkAvailable() : Boolean {
        val cm = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val netCap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
            return netCap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            cm.allNetworks.forEach {
                val info = cm.getNetworkInfo(it)
                if(info != null && info.isConnected()) return true
            }
        }
        return false
    }

}