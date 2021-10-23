package com.example.nala.services.auth

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import javax.inject.Singleton

@Singleton
class GoogleAuthenticator(
    private val activity: Activity,
    private val googleSignInClient: GoogleSignInClient,
    private val onSignIn: () -> Unit,
    private val onSignOut: () -> Unit,
) : Authenticator {

    companion object{
        const val RC_SIGN_IN = 9004
    }

    override fun signIn() {
        onSignIn()
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
}