package es.ua.eps.filmoteca

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (context == null || intent == null) return

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent!!.hasError()) {
            Log.e("GeofenceReceiver", "Error en el geofencing event: ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        val message = when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> context.getString(R.string.enter_geofence)
            Geofence.GEOFENCE_TRANSITION_EXIT -> context.getString((R.string.exit_geofence))
            else -> "Unkown"
        }

        showNotification(context, message)

    }

    private fun showNotification(context: Context, message: String) {
        val channelId = "GeofenceAlerts"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "geofence",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.filmoteca_logo)
            .setContentTitle(context.getString(R.string.activated_geofence))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}