package com.example.second

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddNoteScreen(navController: NavController) {

    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false },
                label = { Text("Заголовок") },
                isError = titleError,
                modifier = Modifier.fillMaxWidth()
            )

            if (titleError) {
                Text(
                    text = "Заголовок не может быть пустым",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Текст заметки") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newNote = Note(
                            title = title,
                            text = text
                        )

                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("new_note", newNote)

                        navController.popBackStack()
                    } else {
                        titleError = true
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}
