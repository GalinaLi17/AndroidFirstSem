package com.example.first

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val incomingText = intent.getStringExtra(EXTRA_TEXT)
        setContent {
                MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SecondScreen(incomingText)
                }
            }
        }
    }
}


@Composable
fun SecondScreen(receivedText: String?) {
    val context = LocalContext.current
    val display = receivedText ?: "Экран 2"

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(text = display)

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val intent = Intent(context, ThirdActivity::class.java)
            if (receivedText != null && receivedText.isNotBlank()) {
                intent.putExtra(EXTRA_TEXT, receivedText)
            }
            context.startActivity(intent)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Открыть экран 3")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Открыть экран 1")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {
    SecondScreen(null)
}