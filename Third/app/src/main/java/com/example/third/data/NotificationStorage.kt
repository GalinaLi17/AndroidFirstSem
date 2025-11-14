package com.example.third.data

object NotificationStorage {
    private val notifications = mutableMapOf<Int, String>()

    fun saveNotification(id: Int, title: String) {
        notifications[id] = title
    }

    fun getTitle(id: Int): String? {
        return notifications[id]
    }
}