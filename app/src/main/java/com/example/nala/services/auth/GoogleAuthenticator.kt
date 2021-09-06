package com.example.nala.services.auth

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.nala.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Singleton

@Singleton
class GoogleAuthenticator(
    private val activity: Activity,
    private val googleSignInClient: GoogleSignInClient,
    private val onSignOut: () -> Unit,
) : Authenticator {

    private val client = OkHttpClient()

    private val requestBody = FormBody.Builder()
        .add("grant_type", "authorization_code")
        .add("client_id", BuildConfig.OAUTH_ID)
        .add("client_secret", BuildConfig.OAUTH_ID)
        .add("redirect_uri", "")
        .add("code", "HERE GOES THE AUTH CODE FROM LOGIN")
        .build()

    private val request = Request.Builder()
        .url("https://oauth2.googleapis.com/token")
        .post(requestBody)
        .build();

    companion object{
        const val RC_GET_AUTH_CODE = 9003
        const val RC_SIGN_IN = 9004
    }

    override fun signIn() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(activity, intent, RC_SIGN_IN, null)
    }

    override fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener { onSignOut() }
    }

    fun isLoggedIn() : Boolean {
        return GoogleSignIn.getLastSignedInAccount(activity) != null
    }

    fun disconnect() {
        googleSignInClient.revokeAccess().addOnCompleteListener { onSignOut() }
    }

    fun getAccessToken() : String {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return ""
            }
            return response.body()!!.string()
        }
    }

    private fun getAuthCode() {
        // Start the retrieval process for a server auth code.  If requested, ask for a refresh
        // token.  Otherwise, only get an access token if a refresh token has been previously
        // retrieved.  Getting a new access token for an existing grant does not require
        // user consent.
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(activity, signInIntent, RC_GET_AUTH_CODE, null)
    }
}