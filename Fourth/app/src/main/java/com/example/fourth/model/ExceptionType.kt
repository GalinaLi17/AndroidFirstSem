package com.example.fourth.model

enum class ExceptionType {
    TOAST, SNACKBAR, RESET
}

sealed class CoroutineException(message: String) : Exception(message) {
    class ToastException(message: String) : CoroutineException(message)
    class SnackbarException(message: String) : CoroutineException(message)
    class ResetException(message: String) : CoroutineException(message)
}