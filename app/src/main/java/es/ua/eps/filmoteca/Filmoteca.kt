package es.ua.eps.filmoteca

import android.app.Application

class Filmoteca : Application() {
    override fun onCreate() {
        super.onCreate()
        ClientManager.init(this)
        FilmDataSource.init(this)
    }
}