package com.example.second

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun NotesListScreen(
    navController: NavController,
    userEmail: String,
    notesViewModel: NotesViewModel = viewModel()

) {
    val notes = notesViewModel.notes

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val newNoteFromHandle = savedStateHandle?.get<Note>("new_note")

    LaunchedEffect(newNoteFromHandle) {
        newNoteFromHandle?.let { note ->
            notesViewModel.addNote(note)
            savedStateHandle.remove<Note>("new_note")
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Ваш e-mail: $userEmail",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (notes.isEmpty()) {
                        item {
                            Text(
                                text = "Заметок пока нет.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(notes) { note ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (note.text.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = note.text,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate("addNote") },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 16.dp)
            ) {
                Text("Добавить заметку")
            }
        }
    }
}
