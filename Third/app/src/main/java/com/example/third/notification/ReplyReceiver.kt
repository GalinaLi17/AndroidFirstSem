package com.example.third.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import androidx.core.app.NotificationManagerCompat
import com.example.third.data.MessageRepository

class ReplyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val replyText = RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence("key_text_reply")
            ?.toString()

        if (!replyText.isNullOrEmpty()) {
            MessageRepository.addMessage(replyText)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancelAll()
        }
    }
}
