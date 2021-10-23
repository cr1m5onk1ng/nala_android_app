package com.example.nala.ui.auth

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.BuildConfig
import com.example.nala.domain.model.auth.UserModel
import com.example.nala.domain.model.utils.AuthState
import com.example.nala.domain.model.utils.ErrorType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
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

    val authRequestPending = mutableStateOf(false)

    val tokenRequestPending = mutableStateOf(false)

    fun setAccount(gAccount: GoogleSignInAccount?) {
        viewModelScope.launch{
            authRequestPending.value = true
            tokenRequestPending.value = true
            val authCode = gAccount?.serverAuthCode
            if(authCode == null) {
                account. value = AuthState.AuthError(ErrorType.LOGIN_ERROR)
            } else {
                val accessToken = requestAccessToken(authCode)
                if(accessToken == null)  {
                    account.value = AuthState.AuthError(ErrorType.OAUTH_ERROR)
                } else {
                    account.value = AuthState.Authenticated(
                        UserModel(
                            username = gAccount.displayName ?: "",
                            email = gAccount.email,
                            photoUrl = gAccount.photoUrl.toString(),
                            token = accessToken,
                        )
                    )
                }
                authRequestPending.value = false
            }
            tokenRequestPending.value = false
        }
    }

    fun invalidateAccount() {
        this.account.value = AuthState.Unauthenticated(null)
    }

    fun setAuthError() {
        this.account.value = AuthState.AuthError(ErrorType.LOGIN_ERROR)
    }

    fun setAuthRequestPending(value: Boolean) {
        authRequestPending.value = value
    }

    fun onGetActivityResult(intent: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            setAuthRequestPending(true)
            val account = task.getResult(ApiException::class.java)
            Log.d("AUTHDEBUG", "User: ${account?.displayName}")
            Log.d("AUTHDEBUG", "Email: ${account?.email}")
            Log.d("AUTHDEBUG", "Profile pic: ${account?.photoUrl}")
            Log.d("AUTHDEBUG", "Auth Code: ${account?.serverAuthCode ?: "Account is NULL"}")
            setAccount(account)
        } catch (e: ApiException) {
            Log.d("AUTHDEBUG", "ERROR: $e")
            setAuthError()
        }
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