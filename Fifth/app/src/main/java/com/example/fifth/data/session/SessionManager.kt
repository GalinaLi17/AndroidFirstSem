package com.example.fifth.data.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    companion object {
        private const val PREF_NAME = "app_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


    fun saveUserSession(userId: Long, userName: String) {
        with(preferences.edit()) {
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_NAME, userName)
            apply()
        }
    }

    fun getCurrentUserId(): Long? {
        return if (preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            preferences.getLong(KEY_USER_ID, -1).takeIf { it != -1L }
        } else {
            null
        }
    }

    fun getCurrentUserName(): String? {
        return if (preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            preferences.getString(KEY_USER_NAME, null)
        } else {
            null
        }
    }

    fun isUserLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        with(preferences.edit()) {
            remove(KEY_USER_ID)
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USER_NAME)
            apply()
        }
    }
}