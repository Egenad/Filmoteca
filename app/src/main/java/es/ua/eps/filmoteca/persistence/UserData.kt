package es.ua.eps.filmoteca.persistence

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import es.ua.eps.filmoteca.USER_NAME
import es.ua.eps.filmoteca.USER_PHOTO_URL
import es.ua.eps.filmoteca.activity.PREFERENCES_NAME

object UserData {
    var userName: String? = null
    var userPhotoUrl: Uri? = null

    fun clear() {
        userName = null
        userPhotoUrl = null
    }

    fun signOut(context: Context){
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .remove(USER_NAME)
            .remove(USER_PHOTO_URL)
            .apply()

        clear()

    }

    fun signIn(context: Context, token: GoogleIdTokenCredential){
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(USER_NAME, token.displayName)
            .putString(USER_PHOTO_URL, token.profilePictureUri.toString())
            .apply()

        userName = token.displayName
        userPhotoUrl = token.profilePictureUri
    }

    fun userSignedIn(context: Context): Boolean{
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val name = sharedPreferences.getString(USER_NAME, null)

        if (name != null) {

            userName = name

            val photoUrl = sharedPreferences.getString(USER_PHOTO_URL, null)
            if(photoUrl != null)
                userPhotoUrl = Uri.parse(photoUrl)

            return true
        }

        return false
    }
}