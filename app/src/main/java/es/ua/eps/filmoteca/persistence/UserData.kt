package es.ua.eps.filmoteca.persistence

import android.net.Uri

object UserData {
    var userId: String? = null
    var userEmail: String? = null
    var userName: String? = null
    var userPhotoUrl: Uri? = null

    fun clear() {
        userId = null
        userEmail = null
        userName = null
        userPhotoUrl = null
    }
}