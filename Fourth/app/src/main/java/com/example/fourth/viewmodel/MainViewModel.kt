package com.example.fourth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fourth.Constants
import com.example.fourth.model.CoroutineException
import com.example.fourth.model.CoroutineSettings
import com.example.fourth.model.DispatcherType
import com.example.fourth.model.ExceptionType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private val _settings = MutableStateFlow(CoroutineSettings())
    val settings = _settings.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var currentJobs = mutableListOf<Job>()

    fun updateCount(count: Int) {
        _settings.value = _settings.value.copy(count = count)
    }

    fun updateDispatcher(dispatcherType: DispatcherType) {
        _settings.value = _settings.value.copy(dispatcherType = dispatcherType)
    }

    fun toggleSequential() {
        _settings.value = _settings.value.copy(
            isSequential = true,
            isParallel = false
        )
    }

    fun toggleParallel() {
        _settings.value = _settings.value.copy(
            isSequential = false,
            isParallel = true
        )
    }

    fun toggleLazy(isLazy: Boolean) {
        _settings.value = _settings.value.copy(isLazy = isLazy)
    }

    fun toggleBackground(isBackgroundEnabled: Boolean) {
        _settings.value = _settings.value.copy(isBackgroundEnabled = isBackgroundEnabled)
    }

    fun launchCoroutines(onException: (ExceptionType) -> Unit, onComplete: (() -> Unit)? = null) {
        _isRunning.value = true
        currentJobs.clear()

        viewModelScope.launch {
            try {
                val jobs = if (_settings.value.isParallel) {
                    launchParallel(onException)
                } else {
                    launchSequential(onException)
                }
                currentJobs.addAll(jobs)
                jobs.joinAll()

                withContext(Dispatchers.Main) {
                    onComplete?.invoke()
                }
            } catch (_: Exception) {

            } finally {
                _isRunning.value = false
            }
        }
    }

    private fun launchParallel(onException: (ExceptionType) -> Unit): List<Job> {
        val dispatcher = getDispatcher(_settings.value.dispatcherType)

        return (1.._settings.value.count).map { index ->
            val startMode = if (_settings.value.isLazy) {
                CoroutineStart.LAZY
            } else {
                CoroutineStart.DEFAULT
            }

            viewModelScope.launch(context = dispatcher) {
                try {
                    heavyOperation(index)
                } catch (e: CoroutineException) {
                    withContext(Dispatchers.Main) {
                        val exceptionType = when (e) {
                            is CoroutineException.ToastException -> ExceptionType.TOAST
                            is CoroutineException.SnackbarException -> ExceptionType.SNACKBAR
                            is CoroutineException.ResetException -> ExceptionType.RESET
                        }
                        onException(exceptionType)
                    }
                } catch (_: CancellationException) {

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onException(ExceptionType.TOAST)
                    }
                    throw e
                }
            }.also { job ->
                if (startMode == CoroutineStart.LAZY) {
                    job.start()
                }
            }
        }
    }

    private fun launchSequential(onException: (ExceptionType) -> Unit): List<Job> {
        val dispatcher = getDispatcher(_settings.value.dispatcherType)
        val startMode = if (_settings.value.isLazy) {
            CoroutineStart.LAZY
        } else {
            CoroutineStart.DEFAULT
        }

        val job = viewModelScope.launch(context = dispatcher) {
            try {
                for (i in 1.._settings.value.count) {
                    heavyOperation(i)
                }
            } catch (e: CoroutineException) {
                withContext(Dispatchers.Main) {
                    val exceptionType = when (e) {
                        is CoroutineException.ToastException -> ExceptionType.TOAST
                        is CoroutineException.SnackbarException -> ExceptionType.SNACKBAR
                        is CoroutineException.ResetException -> ExceptionType.RESET
                    }
                    onException(exceptionType)
                }
            } catch (_: CancellationException) {

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onException(ExceptionType.TOAST)
                }
                throw e
            }
        }

        if (startMode == CoroutineStart.LAZY) {
            job.start()
        }

        return listOf(job)
    }

    private suspend fun heavyOperation(index: Int) {
        val delayTime = Random.nextLong(Constants.MIN_DELAY_MS, Constants.MAX_DELAY_MS + 1)
        delay(delayTime)

        if (delayTime >= Constants.LONG_OPERATION_THRESHOLD && Random.nextFloat() < Constants.EXCEPTION_CHANCE) {
            val exceptionType = ExceptionType.entries.random()
            throw when (exceptionType) {
                ExceptionType.TOAST -> CoroutineException.ToastException("Coroutine $index")
                ExceptionType.SNACKBAR -> CoroutineException.SnackbarException("Coroutine $index")
                ExceptionType.RESET -> CoroutineException.ResetException("Coroutine $index")
            }
        }
    }

    fun cancelAllCoroutines(): Int {
        val unfinished = currentJobs.count { it.isActive }
        currentJobs.forEach {
            it.cancel()
        }
        currentJobs.clear()
        _isRunning.value = false
        return unfinished
    }

    private fun getDispatcher(type: DispatcherType): CoroutineDispatcher {
        return when (type) {
            DispatcherType.DEFAULT -> Dispatchers.Default
            DispatcherType.IO -> Dispatchers.IO
            DispatcherType.MAIN -> Dispatchers.Main
            DispatcherType.UNCONFINED -> Dispatchers.Unconfined
        }
    }
}