package es.ua.eps.filmoteca

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.credentials.GetCredentialException
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import es.ua.eps.filmoteca.activity.MainActivity
import es.ua.eps.filmoteca.persistence.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

object ClientManager {

    private lateinit var appContext: Context
    private lateinit var credentialManager: CredentialManager

    private var interstitialAd: InterstitialAd? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        credentialManager = CredentialManager.create(appContext)
    }

    fun getCredentialManager(): CredentialManager {
        return credentialManager
    }

    fun startGoogleSignInFlow(activityContext: Context) {

        loadInterstitialAd(activityContext)

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

                        onLoginSuccess(activityContext)

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

    fun signIn(context: Context, token: GoogleIdTokenCredential){

        val payload = decodeJwt(token.idToken)
        val id = if(payload != null) payload["sub"].toString() else ""

        UserData.userName = token.displayName
        UserData.userPhotoUrl = token.profilePictureUri
        UserData.userEmail = token.id
        UserData.userId = id
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

    fun onLoginSuccess(activityContext: Context) {

        val activity = activityContext as? Activity

        if (interstitialAd != null && activity != null) {
            interstitialAd?.show(activity)
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    goToMainActivity(activityContext)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    goToMainActivity(activityContext)
                }
            }
        } else {
            goToMainActivity(activityContext)
        }
    }

    private fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d("Ads", "Anuncio cargado correctamente")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    Log.d("Ads", "Error al cargar anuncio: ${adError.message}")
                }
            })
    }

    private fun goToMainActivity(activityContext: Context){
        activityContext.startActivity(Intent(activityContext, MainActivity::class.java))
    }
}