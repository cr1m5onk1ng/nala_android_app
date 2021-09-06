package com.example.nala.ui.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.BuildConfig
import com.example.nala.domain.model.auth.UserModel
import com.example.nala.domain.model.utils.AuthState
import com.example.nala.domain.model.utils.ErrorType
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    val account: MutableState<AuthState<UserModel?>> =
        mutableStateOf(AuthState.Unauthenticated(null))

    val tokenRequestPending = mutableStateOf(false)

    fun setAccount(gAccount: GoogleSignInAccount?) {
        viewModelScope.launch{
            tokenRequestPending.value = true
            val authCode = gAccount?.serverAuthCode
            if(authCode == null) {
                account.value = AuthState.AuthError(ErrorType.ERROR_FETCHING_DATA)
            } else {
                val accessToken = requestAccessToken(authCode)
                account.value = AuthState.Authenticated(
                    UserModel(
                        username = gAccount?.displayName ?: "",
                        email = gAccount?.email,
                        token = accessToken,
                    )
                )
                tokenRequestPending.value = false
            }
        }
    }

    fun invalidateAccount() {
        this.account.value = AuthState.Unauthenticated(null)
    }

    fun setAuthError() {
        this.account.value = AuthState.AuthError(ErrorType.ERROR_FETCHING_DATA)
    }

    private suspend fun requestAccessToken(authCode: String) : String? {
            return withContext(Dispatchers.IO) {
                var ret: String?
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("grant_type", "authorization_code")
                    .add("client_id", BuildConfig.OAUTH_ID)
                    .add("client_secret", BuildConfig.OAUTH_SECRET)
                    .add("redirect_uri", "")
                    .add("code", authCode)
                    .build()

                val request = Request.Builder()
                    .url("https://oauth2.googleapis.com/token")
                    .post(requestBody)
                    .build();
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.d("AUTHDEBUG", "Access Token Request FAILURE")
                    }
                    val parsedData = JSONObject(response.body()!!.string())
                    ret = parsedData.getString("access_token")
                    ret
                }
            }
    }

}