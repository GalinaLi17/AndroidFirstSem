package com.example.third.notification

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.third.MainActivity
import com.example.third.R

class NotificationHelper(private val context: Context) {

    private val channelId = "main_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        id: Int,
        title: String,
        text: String?,
        expandable: Boolean,
        priorityLevel: String,
        openActivity: Boolean,
        enableReply: Boolean
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setPriority(mapPriority(priorityLevel))

        if (!text.isNullOrEmpty()) {
            builder.setContentText(text)
            if (expandable) {
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(text))
            }
        }

        if (openActivity) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("title", title)
                putExtra("text", text)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
        }

        if (enableReply && !text.isNullOrEmpty()) {
            val remoteInput = RemoteInput.Builder("key_text_reply")
                .setLabel(context.getString(R.string.reply_label))
                .build()

            val replyIntent = Intent(context, ReplyReceiver::class.java)
            val replyPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val action = NotificationCompat.Action.Builder(
                R.drawable.ic_reply,
                context.getString(R.string.reply_action),
                replyPendingIntent
            ).addRemoteInput(remoteInput).build()

            builder.addAction(action)
        }

        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(id, builder.build())
            }
        } else {
            notificationManager.notify(id, builder.build())
        }
    }

    private fun mapPriority(priority: String): Int {
        return when (priority) {
            "High" -> NotificationCompat.PRIORITY_HIGH
            "Default" -> NotificationCompat.PRIORITY_DEFAULT
            "Low" -> NotificationCompat.PRIORITY_LOW
            "Min" -> NotificationCompat.PRIORITY_MIN
            else -> NotificationCompat.PRIORITY_DEFAULT
        }
    }
}