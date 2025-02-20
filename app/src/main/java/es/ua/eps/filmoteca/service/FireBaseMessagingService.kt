package es.ua.eps.filmoteca.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.activity.PREFERENCES_NAME
import org.json.JSONObject

const val TOKEN_FCM = "token_fcm"
const val BROAD_CAST_FILM = "DATASOURCE_MODIFIED"

class FirebaseMessagingService : FirebaseMessagingService() {

    private var token : String? = null

    fun askToken(context: Context) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FireBase", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                token = task.result

                val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
                sharedPreferences.edit()
                    .putString(TOKEN_FCM, token)
                    .apply()

                Log.d("FireBase", "FCM registration Token: $token")
            })
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
        Log.d("FireBase", "Refreshed token: $token")

        val sharedPref = applicationContext.getSharedPreferences("_", MODE_PRIVATE)
        sharedPref.edit().putString("fcm_token", token).apply()
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            mostrarNotificacion(it.title ?: "Notificación", it.body ?: "Mensaje recibido")
        }

        if (remoteMessage.data.isNotEmpty()) {

            val type = remoteMessage.data["operation_type"]
            val movieTitle = remoteMessage.data["movie_title"]

            if(type != null && movieTitle != null){

                try {

                    val filmData = FilmData(
                        title = movieTitle,
                        director = remoteMessage.data["movie_director"],
                        year = remoteMessage.data["movie_year"],
                        genre = remoteMessage.data["movie_genre"],
                        format = remoteMessage.data["movie_format"],
                        imageUrl = remoteMessage.data["movie_imageUrl"],
                        imdbUrl = remoteMessage.data["movie_imdbUrl"],
                        comments = remoteMessage.data["movie_comments"]
                    )

                    when (type) {
                        "add" -> {
                            Log.d("FCM", "Add movie: $movieTitle, with details: $filmData")
                            FilmDataSource.addFireBaseMovie(filmData)
                        }

                        "delete" -> {
                            Log.d("FCM", "Delete movie: $movieTitle")
                            FilmDataSource.deleteFireBaseMovie(movieTitle)
                        }
                    }

                    val intent = Intent(BROAD_CAST_FILM)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }catch (ex: Exception){
                    Log.e("FCM", "Error processing message: $ex")
                }
            }
        }
    }

    private fun mostrarNotificacion(title: String, message: String) {
        val channelId = "default_channel"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notificaciones", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.filmoteca_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            NotificationManagerCompat.from(this).notify(1, notificationBuilder.build())
        } else {
            Log.e("Notification", "Notification permission is not granted")
        }
    }
}
