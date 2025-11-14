package com.example.third.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.third.R
import com.example.third.data.NotificationStorage
import com.example.third.notification.NotificationHelper
import kotlin.random.Random

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    var lastNotificationId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var expandText by remember { mutableStateOf(false) }
    var openActivity by remember { mutableStateOf(false) }
    var enableReply by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val priorities = listOf(
        stringResource(R.string.priority_high),
        stringResource(R.string.priority_default),
        stringResource(R.string.priority_low),
        stringResource(R.string.priority_min)
    )
    var selectedPriority by remember {
        mutableStateOf("Default")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.notification_title_hint)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = title.text.isEmpty()
        )

        OutlinedTextField(
            value = message,
            onValueChange = {
                message = it
                if (message.text.isEmpty()) {
                    expandText = false
                    enableReply = false
                }
            },
            label = { Text(stringResource(R.string.notification_text_hint)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.expand_text_label))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = expandText,
                onCheckedChange = { expandText = it },
                enabled = message.text.isNotEmpty()
            )
        }

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${stringResource(R.string.priority_label)}: $selectedPriority")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                priorities.forEach { priority ->
                    DropdownMenuItem(
                        text = { Text(priority) },
                        onClick = {
                            selectedPriority = priority
                            expanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.open_activity_label))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = openActivity,
                onCheckedChange = { openActivity = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.reply_action_label))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = enableReply,
                onCheckedChange = { enableReply = it },
                enabled = message.text.isNotEmpty()
            )
        }

        Button(
            onClick = {
                val id = Random.nextInt(1000, 9999)
                lastNotificationId = id

                NotificationStorage.saveNotification(id, title.text)

                notificationHelper.showNotification(
                    id = id,
                    title = title.text,
                    text = message.text.ifEmpty { null },
                    expandable = expandText,
                    priorityLevel = selectedPriority,
                    openActivity = openActivity,
                    enableReply = enableReply
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = title.text.isNotEmpty()
        ) {
            Text(stringResource(R.string.create_notification_button))
        }

        if (lastNotificationId != null) {
            Text(
                text = "ID созданного уведомления: $lastNotificationId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}