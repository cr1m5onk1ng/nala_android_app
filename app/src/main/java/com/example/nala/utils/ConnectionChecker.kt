package com.example.nala.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.Url
import java.net.URL
import javax.inject.Inject

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