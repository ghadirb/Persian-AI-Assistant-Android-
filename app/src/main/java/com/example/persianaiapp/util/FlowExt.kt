package com.example.persianaiapp.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Extension function to collect a Flow in a lifecycle-aware manner
 */
fun <T> Flow<T>.collectIn(
    scope: CoroutineScope,
    action: suspend (T) -> Unit
) {
    scope.launch {
        collect { action(it) }
    }
}

/**
 * Extension function to collect a Flow in a lifecycle-aware manner with lifecycle scope
 */
fun <T> Flow<T>.collectInLifecycle(
    scope: androidx.lifecycle.LifecycleCoroutineScope,
    minActiveState: androidx.lifecycle.Lifecycle.State = androidx.lifecycle.Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    scope.launchWhenCreated {
        this@collectInLifecycle.collect { action(it) }
    }
}

/**
 * Extension function to collect a StateFlow in a lifecycle-aware manner
 */
fun <T> StateFlow<T>.observe(
    scope: CoroutineScope,
    action: (T) -> Unit
) {
    scope.launch {
        collect { action(it) }
    }
}

/**
 * Extension function to collect a SharedFlow in a lifecycle-aware manner
 */
fun <T> SharedFlow<T>.observe(
    scope: CoroutineScope,
    action: (T) -> Unit
) {
    scope.launch {
        collect { action(it) }
    }
}

/**
 * Extension function to create a MutableStateFlow with initial value
 */
fun <T> mutableStateFlow(initialValue: T): MutableStateFlow<T> {
    return MutableStateFlow(initialValue)
}

/**
 * Extension function to create a MutableSharedFlow with replay
 */
fun <T> mutableSharedFlow(
    replay: Int = 0,
    extraBufferCapacity: Int = 0,
    onBufferOverflow: kotlinx.coroutines.channels.BufferOverflow = kotlinx.coroutines.channels.BufferOverflow.SUSPEND
): MutableSharedFlow<T> {
    return MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
}

/**
 * Extension function to emit to a MutableStateFlow in a safe way
 */
fun <T> MutableStateFlow<T>.safeEmit(value: T) {
    if (this.value != value) {
        this.value = value
    }
}

/**
 * Extension function to emit to a MutableSharedFlow in a safe way
 */
suspend fun <T> MutableSharedFlow<T>.safeEmit(value: T) {
    if (!tryEmit(value)) {
        emit(value)
    }
}

/**
 * Extension function to collect the latest value from a Flow
 */
fun <T> Flow<T>.collectLatestIn(
    scope: CoroutineScope,
    action: suspend (T) -> Unit
) {
    scope.launch {
        collectLatest { action(it) }
    }
}

/**
 * Extension function to collect the latest value from a Flow in a lifecycle-aware manner
 */
fun <T> Flow<T>.collectLatestInLifecycle(
    scope: androidx.lifecycle.LifecycleCoroutineScope,
    minActiveState: androidx.lifecycle.Lifecycle.State = androidx.lifecycle.Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    scope.launchWhenCreated {
        this@collectLatestInLifecycle.collectLatest { action(it) }
    }
}

/**
 * Extension function to debounce a Flow
 */
fun <T> Flow<T>.debounce(timeout: Long): Flow<T> {
    return kotlinx.coroutines.flow.debounce(timeout)
}

/**
 * Extension function to throttle a Flow
 */
fun <T> Flow<T>.throttleFirst(timeout: Long): Flow<T> {
    return kotlinx.coroutines.flow.throttleFirst(timeout)
}

/**
 * Extension function to filter out null values from a Flow
 */
fun <T> Flow<T?>.filterNotNull(): Flow<T> {
    return kotlinx.coroutines.flow.filterNotNull()
}

/**
 * Extension function to map a Flow with a suspend function
 */
fun <T, R> Flow<T>.mapSuspend(transform: suspend (T) -> R): Flow<R> {
    return kotlinx.coroutines.flow.map { transform(it) }
}

/**
 * Extension function to filter a Flow with a suspend predicate
 */
fun <T> Flow<T>.filterSuspend(predicate: suspend (T) -> Boolean): Flow<T> {
    return kotlinx.coroutines.flow.filter { predicate(it) }
}

/**
 * Extension function to combine two Flows
 */
fun <T1, T2, R> Flow<T1>.combine(
    flow: Flow<T2>,
    transform: suspend (T1, T2) -> R
): Flow<R> {
    return kotlinx.coroutines.flow.combine(this, flow, transform)
}

/**
 * Extension function to zip two Flows
 */
fun <T1, T2, R> Flow<T1>.zip(
    flow: Flow<T2>,
    transform: suspend (T1, T2) -> R
): Flow<R> {
    return kotlinx.coroutines.flow.zip(flow, transform)
}

/**
 * Extension function to handle loading states
 */
fun <T> Flow<T>.handleLoading(
    onLoading: suspend () -> Unit,
    onComplete: suspend () -> Unit = {},
    onError: suspend (Throwable) -> Unit = {}
): Flow<T> {
    return kotlinx.coroutines.flow.onStart { onLoading() }
        .catch { e -> onError(e) }
        .onCompletion { onComplete() }
}
