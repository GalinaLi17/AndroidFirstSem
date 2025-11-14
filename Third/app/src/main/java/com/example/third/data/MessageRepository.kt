package com.example.third.data

object MessageRepository {
    private val _messages = mutableListOf<String>()

    val messages: List<String>
        get() = _messages.toList()

    fun addMessage(message: String) {
        _messages.add(message)
    }

    fun clear() {
        _messages.clear()
    }
}