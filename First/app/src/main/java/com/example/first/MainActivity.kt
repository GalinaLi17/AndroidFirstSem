package com.example.first

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FirstScreen()
                }
            }
        }
    }
}

@Composable
fun FirstScreen() {

    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {


        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Введите текст") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))


        Button(onClick = {

            val intent = Intent(context, SecondActivity::class.java)
            if (text.isNotBlank()) {
                intent.putExtra(EXTRA_TEXT, text)
            }
            context.startActivity(intent)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Открыть экран 2")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val intent = Intent(context, ThirdActivity::class.java)
            if (text.isNotBlank()) {
                intent.putExtra(EXTRA_TEXT, text)
            }
            context.startActivity(intent)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Открыть экран 3")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FirstScreenPreview() {
    FirstScreen()
}
