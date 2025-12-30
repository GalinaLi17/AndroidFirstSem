package com.example.fifth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fifth.FifthApplication
import com.example.fifth.data.repository.AppRepository
import com.example.fifth.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddContentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val sessionManager: SessionManager
    private var currentUserId: Long? = null

    init {
        val app = application as FifthApplication
        repository = app.repository
        sessionManager = app.sessionManager
        currentUserId = sessionManager.getCurrentUserId()
    }

    private val _uiState = MutableStateFlow(AddContentUiState())
    val uiState: StateFlow<AddContentUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description, descriptionError = null) }
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(category = category, categoryError = null) }
    }

    fun updateRating(rating: Float) {
        _uiState.update { it.copy(rating = rating, ratingError = null) }
    }

    fun addContent() {
        val title = _uiState.value.title.trim()
        val description = _uiState.value.description.trim()
        val category = _uiState.value.category.trim()
        val rating = _uiState.value.rating

        var hasError = false

        if (title.isEmpty()) {
            _uiState.update { it.copy(titleError = "Введите название") }
            hasError = true
        }

        if (description.isEmpty()) {
            _uiState.update { it.copy(descriptionError = "Введите описание") }
            hasError = true
        }

        if (category.isEmpty()) {
            _uiState.update { it.copy(categoryError = "Выберите категорию") }
            hasError = true
        }

        if (rating < 0f || rating > 5f) {
            _uiState.update { it.copy(ratingError = "Рейтинг должен быть от 0 до 5") }
            hasError = true
        }

        if (hasError) {
            return
        }

        currentUserId?.let { userId ->
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                try {
                    val contentId = repository.addContent(
                        userId = userId,
                        title = title,
                        description = description,
                        category = category,
                        rating = rating,
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Контент успешно добавлен"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка при добавлении контента"
                        )
                    }
                }
            }
        } ?: run {
            _uiState.update {
                it.copy(
                    error = "Пользователь не авторизован"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AddContentUiState()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(isSuccess = false, successMessage = null) }
    }
}

class AddContentViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddContentViewModel::class.java)) {
            return AddContentViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class AddContentUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val rating: Float = 3.0f,
    val posterUrl: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val ratingError: String? = null
)