package com.example.fourth.model

data class CoroutineSettings(
    val count: Int = 10,
    val dispatcherType: DispatcherType = DispatcherType.DEFAULT,
    val isSequential: Boolean = true,
    val isParallel: Boolean = false,
    val isLazy: Boolean = false,
    val isBackgroundEnabled: Boolean = true
)