package es.ua.eps.filmoteca.persistence

import android.net.Uri

object UserData {
    var userToken: String? = null
    var userId: String? = null
    var userName: String? = null
    var userPhotoUrl: Uri? = null

    fun clear() {
        userToken = null
        userId = null
        userName = null
        userPhotoUrl = null
    }
}