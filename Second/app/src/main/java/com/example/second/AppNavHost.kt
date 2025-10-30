package com.example.second

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }

        composable("notesList/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            NotesListScreen(navController, email)
        }

        composable("addNote") {
            AddNoteScreen(navController)
        }
    }
}
