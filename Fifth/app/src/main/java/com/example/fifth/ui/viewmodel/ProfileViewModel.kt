package com.example.fifth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fifth.FifthApplication
import com.example.fifth.data.database.AppDatabase
import com.example.fifth.data.repository.AppRepository
import com.example.fifth.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val sessionManager: SessionManager
    private var currentUserId: Long? = null

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        val context = application.applicationContext
        repository = if (context is FifthApplication) {
            context.repository
        } else {
            val database = AppDatabase.getDatabase(context)
            AppRepository(database)
        }

        sessionManager = SessionManager(context)
        currentUserId = sessionManager.getCurrentUserId()

        loadUserData()
    }

    fun loadUserData() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                try {
                    val user = repository.getUserById(userId)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            error = if (user == null) "Пользователь не найден" else null
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка загрузки данных: ${e.message}"
                        )
                    }
                }
            }
        } ?: run {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Пользователь не авторизован"
                )
            }
        }
    }


    fun logout() {
        sessionManager.clearSession()
        _uiState.update { it.copy(isLoggedOut = true) }
    }

    fun deleteAccount() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                try {
                    repository.softDeleteUser(userId)
                    sessionManager.clearSession()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAccountDeleted = true,
                            error = null
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка при удалении аккаунта"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearLogoutState() {
        _uiState.update { it.copy(isLoggedOut = false) }
    }

    fun clearDeleteState() {
        _uiState.update { it.copy(isAccountDeleted = false) }
    }
}


class ProfileViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: com.example.fifth.data.database.UserEntity? = null,
    val error: String? = null,
    val isLoggedOut: Boolean = false,
    val isAccountDeleted: Boolean = false
)