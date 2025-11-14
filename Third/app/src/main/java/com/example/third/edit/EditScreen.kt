package com.example.third.edit

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.example.third.data.NotificationStorage
import com.example.third.notification.NotificationHelper

@Composable
fun EditScreen() {
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    var notificationId by remember { mutableStateOf(TextFieldValue("")) }
    var newText by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Редактирование уведомления",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = notificationId,
            onValueChange = { notificationId = it },
            label = { Text("ID уведомления") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = newText,
            onValueChange = { newText = it },
            label = { Text("Новый текст уведомления") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        Button(
            onClick = {
                val id = notificationId.text.toIntOrNull()
                if (id != null) {
                    val notificationManager = NotificationManagerCompat.from(context)
                    val activeNotifications = notificationManager.activeNotifications
                    val exists = activeNotifications.any { it.id == id }

                    if (exists) {
                        val savedTitle = NotificationStorage.getTitle(id)

                        notificationHelper.showNotification(
                            id = id,
                            title = savedTitle ?: "Уведомление",
                            text = newText.text.ifEmpty { "(без текста)" },
                            expandable = true,
                            priorityLevel = "Default",
                            openActivity = false,
                            enableReply = false
                        )
                        Toast.makeText(context, "Уведомление обновлено", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Уведомление с таким ID не найдено",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(context, "Введите корректный ID", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Обновить уведомление")
        }

        Button(
            onClick = {
                val notificationManager = NotificationManagerCompat.from(context)
                val active = notificationManager.activeNotifications
                if (active.isEmpty()) {
                    Toast.makeText(context, "Нет активных уведомлений", Toast.LENGTH_SHORT).show()
                } else {
                    notificationManager.cancelAll()
                    Toast.makeText(context, "Все уведомления удалены", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Очистить все уведомления")
        }
    }
}