package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.credentials.GetCredentialException
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import es.ua.eps.filmoteca.activity.MainActivity
import es.ua.eps.filmoteca.activity.PREFERENCES_NAME
import es.ua.eps.filmoteca.persistence.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


const val USER_NAME         = "user_name"
const val USER_PHOTO_URL    = "user_photo_url"
const val USER_EMAIL        = "user_email"
const val USER_ID           = "user_id"

object ClientManager {

    private lateinit var appContext: Context
    private lateinit var credentialManager: CredentialManager

    fun init(context: Context) {
        appContext = context.applicationContext
        credentialManager = CredentialManager.create(appContext)
    }
    fun startGoogleSignInFlow(activityContext: Context) {

        CoroutineScope(Dispatchers.Main).launch {

            val googleSignInOption = GetSignInWithGoogleOption.Builder(appContext.getString(R.string.google_cloud_client_id))
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
                handleGoogleCredential(response, activityContext)
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
                context = appContext,
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
                context = appContext,
                request = credentialRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private fun handleGoogleCredential(response: GetCredentialResponse, activityContext: Context) {

        when(val credential = response.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        handleAccount(googleIdTokenCredential)

                        activityContext.startActivity(Intent(activityContext, MainActivity::class.java))

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
            signIn(appContext, token)
        } else {
            Log.d("GoogleSignIn", "No se pudo obtener la cuenta")
        }
    }

    fun signOut(context: Context){
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .remove(USER_NAME)
            .remove(USER_PHOTO_URL)
            .remove(USER_ID)
            .remove(USER_EMAIL)
            .apply()

        UserData.clear()

    }

    fun signIn(context: Context, token: GoogleIdTokenCredential){
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        val payload = decodeJwt(token.idToken)
        val id = if(payload != null) payload["sub"].toString() else ""

        sharedPreferences.edit()
            .putString(USER_NAME, token.displayName)
            .putString(USER_PHOTO_URL, token.profilePictureUri.toString())
            .putString(USER_ID, id)
            .putString(USER_EMAIL, token.id)
            .apply()

        UserData.userName = token.displayName
        UserData.userPhotoUrl = token.profilePictureUri
        UserData.userEmail = token.id
        UserData.userId = id
    }

    fun userSignedIn(context: Context): Boolean{
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val name = sharedPreferences.getString(USER_NAME, null)

        if (name != null) {

            UserData.userName = name

            val photoUrl = sharedPreferences.getString(USER_PHOTO_URL, null)
            if(photoUrl != null)
                UserData.userPhotoUrl = Uri.parse(photoUrl)

            val id = sharedPreferences.getString(USER_ID, null)
            if(id != null)
                UserData.userId = id

            val email = sharedPreferences.getString(USER_EMAIL, null)
            if(email != null)
                UserData.userEmail = email

            return true
        }

        return false
    }

    fun decodeJwt(token: String): JSONObject? {
        val parts = token.split(".")
        return try {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = JSONObject(payload)
            json
        } catch (e: Exception) {
            null
        }
    }

    fun disconnectAccount(activityContext: Context) {
        signOut(activityContext)

        CoroutineScope(Dispatchers.Main).launch {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }

        val logoutUrl = "https://accounts.google.com/logout"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(logoutUrl))
        activityContext.startActivity(intent)
    }
}