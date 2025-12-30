package com.example.fifth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fifth.FifthApplication
import com.example.fifth.data.repository.AccountSoftDeletedException
import com.example.fifth.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val app = application as FifthApplication

    init {
        repository = app.repository
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    private val _softDeletedAccount = MutableStateFlow<SoftDeletedAccount?>(null)
    val softDeletedAccount: StateFlow<SoftDeletedAccount?> = _softDeletedAccount.asStateFlow()

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.loginUser(login, password)
                app.sessionManager.saveUserSession(user.id, user.name)

                _authState.value = AuthState.Success(user)
            } catch (e: AccountSoftDeletedException) {
                _softDeletedAccount.value = SoftDeletedAccount(
                    userId = e.user.id,
                    login = e.user.login,
                    deletionDate = e.user.deletionDate
                )
                _authState.value = AuthState.Error("Аккаунт был удален. Вы можете его восстановить.")
            } catch (e: IllegalArgumentException) {
                _authState.value = AuthState.Error(e.message ?: "Ошибка авторизации")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Произошла неизвестная ошибка")
            }
        }
    }

    fun register(login: String, password: String, name: String) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val userId = repository.registerUser(login, password, name)
                _registrationState.value = RegistrationState.Success(userId)
            } catch (e: IllegalArgumentException) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Ошибка регистрации")
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error("Произошла неизвестная ошибка")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.Idle
    }

    fun restoreAccount(userId: Long) {
        viewModelScope.launch {
            repository.restoreUser(userId)
            _softDeletedAccount.value = null
        }
    }

    fun permanentlyDeleteAccount(userId: Long) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            user?.let {
                repository.permanentlyDeleteUser(it)
            }
            _softDeletedAccount.value = null
        }
    }
}


class AuthViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: com.example.fifth.data.database.UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val userId: Long) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

data class SoftDeletedAccount(
    val userId: Long,
    val login: String,
    val deletionDate: java.util.Date?
)