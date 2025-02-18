package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.content.Context
import android.credentials.GetCredentialException
import android.os.Build
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientManager(private var context: Context) {

    private var credentialManager: CredentialManager = CredentialManager.create(context)

    fun startGoogleSignInFlow() {
        CoroutineScope(Dispatchers.Main).launch {

            val googleSignInOption = GetSignInWithGoogleOption
                .Builder("13801236933-9l6mdg6uv5naqia3lvslij5eceo1s13b.apps.googleusercontent.com")
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
    ):
            GetCredentialResponse? {
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
        val googleCredential = response.credential

        println(googleCredential)
    }
}