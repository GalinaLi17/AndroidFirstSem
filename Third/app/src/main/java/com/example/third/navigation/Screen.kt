package com.example.third.navigation

sealed class Screen(val route: String, val title: String) {
    object Settings : Screen("settings", "Настройки")
    object Edit : Screen("edit", "Редактирование")
    object Messages : Screen("messages", "Сообщения")
}