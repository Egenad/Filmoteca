package es.ua.eps.filmoteca

import android.app.Application
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Filmoteca : Application() {
    override fun onCreate() {
        super.onCreate()
        ClientManager.init(this)
        FilmDataSource.init(this)

        MobileAds.initialize(this) {}
    }
}