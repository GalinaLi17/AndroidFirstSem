package com.example.third.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.third.edit.EditScreen
import com.example.third.messages.MessagesScreen
import com.example.third.settings.SettingsScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Settings.route
    ) {
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.Edit.route) {
            EditScreen()
        }
        composable(Screen.Messages.route) {
            MessagesScreen()
        }
    }
}

