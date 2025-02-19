package es.ua.eps.filmoteca.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var token : String? = null

    fun askToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    Log.w("FireBase", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                token = task.result
                Log.d("FireBase", "FCM registration Token: ${token}")
            })
    }

    override fun onNewToken(newToken: String) {
        token = newToken
        Log.d("FireBase", "Refreshed token: $token")
        /*Handler(Looper.getMainLooper()).post {
            for (func in onNewTokenListeners) {
                func(newToken)
            }
        }*/
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Log para depuración
        Log.d("FCM", "Mensaje recibido: ${remoteMessage.data}")

        // Si el mensaje tiene una notificación, la mostramos
        remoteMessage.notification?.let {
            mostrarNotificacion(it.title ?: "Notificación", it.body ?: "Mensaje recibido")
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
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Cambia por un ícono de tu app
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
