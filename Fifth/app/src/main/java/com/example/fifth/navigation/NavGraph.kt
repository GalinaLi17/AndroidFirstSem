package com.example.fifth.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fifth.FifthApplication
import com.example.fifth.ui.screens.auth.LoginScreen
import com.example.fifth.ui.screens.auth.RegistrationScreen
import com.example.fifth.ui.screens.auth.RestoreAccountScreen
import com.example.fifth.ui.screens.content.AddContentScreen
import com.example.fifth.ui.screens.content.ContentListScreen
import com.example.fifth.ui.screens.profile.ProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screens.Login.route
) {
    val context = LocalContext.current
    val sessionManager = (context.applicationContext as FifthApplication).sessionManager

    var initialDestination by remember { mutableStateOf(startDestination) }

    LaunchedEffect(Unit) {
        if (sessionManager.isUserLoggedIn()) {
            initialDestination = Screens.ContentList.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination
    ) {
        composable(Screens.Login.route) {
            LoginScreen(
                onNavigateToRegistration = {
                    navController.navigate(Screens.Registration.route)
                },
                onLoginSuccess = { userId ->
                    navController.navigate(Screens.ContentList.route) {
                        popUpTo(Screens.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRestoreAccount = { userId ->
                    navController.navigate("${Screens.RestoreAccount.route}/$userId")
                }
            )
        }

        composable(Screens.Registration.route) {
            RegistrationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = { userId ->
                    navController.popBackStack()
                }
            )
        }

        composable(Screens.ContentList.route) {
            ContentListScreen(navController = navController)
        }

        composable(Screens.AddContent.route) {
            AddContentScreen(navController = navController)
        }

        composable(Screens.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable("${Screens.RestoreAccount.route}/{userId}") { backStackEntry ->
            val userIdString = backStackEntry.arguments?.getString("userId")
            val userId = userIdString?.toLongOrNull()
            if (userId != null) {
                RestoreAccountScreen(
                    userId = userId,
                    onRestoreSuccess = {
                        navController.navigate(Screens.Login.route) {
                            popUpTo(Screens.Login.route) { inclusive = true }
                        }
                    },
                    onDeleteSuccess = {
                        navController.navigate(Screens.Login.route) {
                            popUpTo(Screens.Login.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}


sealed class Screens(val route: String) {
    object Login : Screens("login")
    object Registration : Screens("registration")
    object ContentList : Screens("content_list")
    object AddContent : Screens("add_content")
    object Profile : Screens("profile")
    object RestoreAccount : Screens("restore_account")
}