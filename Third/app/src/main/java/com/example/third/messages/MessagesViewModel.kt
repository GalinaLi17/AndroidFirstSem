package com.example.third.messages

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.third.data.MessageRepository

class MessagesViewModel : ViewModel() {
    private val _messages = mutableStateListOf<String>()
    val messages: List<String> get() = _messages

    init {
        _messages.addAll(MessageRepository.messages)
    }

    fun addMessage(message: String) {
        MessageRepository.addMessage(message)
        _messages.clear()
        _messages.addAll(MessageRepository.messages)
    }

    fun clearMessages() {
        MessageRepository.clear()
        _messages.clear()
    }
}

