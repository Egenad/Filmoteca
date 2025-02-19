package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.credentials.GetCredentialException
import android.os.Build
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import es.ua.eps.filmoteca.activity.MainActivity
import es.ua.eps.filmoteca.persistence.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientManager(private var context: Context) {

    private var credentialManager: CredentialManager = CredentialManager.create(context)

    fun startGoogleSignInFlow() {
        CoroutineScope(Dispatchers.Main).launch {

            val googleSignInOption = GetSignInWithGoogleOption
                .Builder("13801236933-p9q7ceh6o576pan3ij2bnupcgd2j5bn1.apps.googleusercontent.com") // Web type client Id
                .build()

            val credentialRequest = GetCredentialRequest
                .Builder()
                .addCredentialOption(googleSignInOption)
                .build()

            val response: GetCredentialResponse? = if (Build.VERSION.SDK_INT >= 34) {
                getCredentialResponseAPI34(credentialManager, credentialRequest)
            } else {
                getCredentialResponseOld(credentialManager, credentialRequest)
            }

            if (response != null) {
                handleGoogleCredential(response)
            }
        }
    }

    private suspend fun getCredentialResponseAPI34(
        credentialManager: CredentialManager,
        credentialRequest: GetCredentialRequest
    ): GetCredentialResponse? {
        var response: GetCredentialResponse? = null
        try {
            response = credentialManager.getCredential(
                context = context,
                request = credentialRequest
            )
        } catch (@SuppressLint("NewApi") e: GetCredentialException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private suspend fun getCredentialResponseOld(
        credentialManager: CredentialManager,
        credentialRequest: GetCredentialRequest
    ): GetCredentialResponse? {
        var response: GetCredentialResponse? = null
        try {
            response = credentialManager.getCredential(
                context = context,
                request = credentialRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private fun handleGoogleCredential(response: GetCredentialResponse) {

        when(val credential = response.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        handleAccount(googleIdTokenCredential)

                        context.startActivity(Intent(context, MainActivity::class.java))

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("Google", "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e("Google", "Unexpected type of credential")
                }
            }
        }
    }

    private fun handleAccount(token: GoogleIdTokenCredential?) {
        if (token != null) {
            UserData.userToken = token.idToken
            UserData.userId = token.id
            UserData.userName = token.displayName
            UserData.userPhotoUrl = token.profilePictureUri
        } else {
            Log.d("GoogleSignIn", "No se pudo obtener la cuenta")
        }
    }
}