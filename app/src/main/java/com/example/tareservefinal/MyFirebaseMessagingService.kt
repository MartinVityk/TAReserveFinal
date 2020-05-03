package com.example.tareservefinal

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService(){

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        println("JIM" + p0.data.get("body")+ p0.data.get("title"))
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, "123")
            .setContentTitle(p0.data.get("title"))
            .setContentText(p0.data.get("body"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }


}