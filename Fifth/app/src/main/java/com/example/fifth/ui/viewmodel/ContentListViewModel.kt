package com.example.fifth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fifth.FifthApplication
import com.example.fifth.data.database.AppDatabase
import com.example.fifth.data.database.ContentEntity
import com.example.fifth.data.repository.AppRepository
import com.example.fifth.data.session.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContentListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    private val sessionManager: SessionManager
    private var currentUserId: Long? = null

    private val _uiState = MutableStateFlow(ContentListUiState())
    val uiState: StateFlow<ContentListUiState> = _uiState.asStateFlow()

    private var contentCollectionJob: Job? = null

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

        loadContent()
    }

    private fun collectContentFlow(flow: kotlinx.coroutines.flow.Flow<List<ContentEntity>>) {
        contentCollectionJob?.cancel()
        contentCollectionJob = null

        contentCollectionJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                flow.collect { contentList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            contentList = contentList,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки контента: ${e.message ?: "Неизвестная ошибка"}"
                    )
                }
            }
        }
    }

    fun loadContent() {
        currentUserId?.let { userId ->
            collectContentFlow(repository.getUserContentSortedByDateNewest(userId))
        } ?: run {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Пользователь не авторизован"
                )
            }
        }
    }

    fun sortByTitle() {
        currentUserId?.let { userId ->
            collectContentFlow(repository.getUserContentSortedByTitle(userId))
            _uiState.update { it.copy(currentSortType = SortType.BY_TITLE) }
        } ?: showAuthError()
    }

    fun sortByDateNewest() {
        currentUserId?.let { userId ->
            collectContentFlow(repository.getUserContentSortedByDateNewest(userId))
            _uiState.update { it.copy(currentSortType = SortType.BY_DATE_NEWEST) }
        } ?: showAuthError()
    }

    fun sortByDateOldest() {
        currentUserId?.let { userId ->
            collectContentFlow(repository.getUserContentSortedByDateOldest(userId))
            _uiState.update { it.copy(currentSortType = SortType.BY_DATE_OLDEST) }
        } ?: showAuthError()
    }

    fun sortByRatingHigh() {
        currentUserId?.let { userId ->
            collectContentFlow(repository.getUserContentSortedByRatingHigh(userId))
            _uiState.update { it.copy(currentSortType = SortType.BY_RATING_HIGH) }
        } ?: showAuthError()
    }

    fun sortByRatingLow() {
        currentUserId?.let { userId ->
            collectContentFlow(repository.getUserContentSortedByRatingLow(userId))
            _uiState.update { it.copy(currentSortType = SortType.BY_RATING_LOW) }
        } ?: showAuthError()
    }

    private fun showAuthError() {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = "Пользователь не авторизован"
            )
        }
    }


    fun deleteContent(contentId: Long) {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                val content = uiState.value.contentList.find { it.id == contentId }
                content?.let {
                    repository.deleteContent(it)
                    loadContent()
                }
            }
        }
    }

    fun searchContent(query: String) {
        currentUserId?.let { userId ->
            if (query.isBlank()) {
                _uiState.update { it.copy(isSearching = false, searchQuery = "") }
                loadContent()
            } else {
                _uiState.update { it.copy(isSearching = true, searchQuery = query) }
                collectContentFlow(repository.searchContent(userId, query))
            }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                isSearching = false,
                searchQuery = ""
            )
        }
        loadContent()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}


class ContentListViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentListViewModel::class.java)) {
            return ContentListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


data class ContentListUiState(
    val isLoading: Boolean = false,
    val contentList: List<ContentEntity> = emptyList(),
    val error: String? = null,
    val currentSortType: SortType = SortType.BY_DATE_NEWEST,
    val isSearching: Boolean = false,
    val searchQuery: String = ""
)


enum class SortType {
    BY_TITLE,
    BY_DATE_NEWEST,
    BY_DATE_OLDEST,
    BY_RATING_HIGH,
    BY_RATING_LOW
}