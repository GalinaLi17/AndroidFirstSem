package com.example.fifth

import android.app.Application
import com.example.fifth.data.database.AppDatabase
import com.example.fifth.data.repository.AppRepository
import com.example.fifth.data.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FifthApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database) }
    val sessionManager by lazy { SessionManager(this) }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.cleanupExpiredAccounts()
        }
    }
}