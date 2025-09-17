package com.example.persianaiapp.util

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>() {
        override fun toString() = "Success[data=$data]"
    }

    data class Error(val message: String, val cause: Exception? = null) : Result<Nothing>() {
        override fun toString() = "Error[message='$message', cause=$cause]"
    }

    object Loading : Result<Nothing>() {
        override fun toString() = "Loading"
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(message: String, cause: Exception? = null): Result<Nothing> = Error(message, cause)
        fun <T> loading(): Result<T> = Loading
    }
}

/**
 * `true` if [Result] is of type [Result.Success] & holds non-null [Result.Success.data].
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [Result.Success] or the original [Result.Error] if it is [Result.Error].
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Returns the encapsulated value if this instance represents [Result.Success] or `null` if it is [Result.Error].
 */
fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    else -> null
}

/**
 * Returns the encapsulated value if this instance represents [Result.Success] or the [defaultValue] if it is [Result.Error].
 */
fun <T> Result<T>.getOrDefault(defaultValue: T): T = when (this) {
    is Result.Success -> data
    else -> defaultValue
}

/**
 * Returns the encapsulated value if this instance represents [Result.Success] or throws an exception if it is [Result.Error].
 */
fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> throw IllegalStateException("No value present")
    is Result.Loading -> throw IllegalStateException("Still loading")
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [Result.Success] or the original [Result.Error] if it is [Result.Error].
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return when (this) {
        is Result.Success -> transform(data)
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [Result.Success].
 * Returns the original `Result` unchanged.
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [Result.Error].
 * Returns the original `Result` unchanged.
 */
inline fun <T> Result<T>.onError(action: (String, Exception?) -> Unit): Result<T> {
    if (this is Result.Error) action(message, cause)
    return this
}

/**
 * Performs the given [action] if this instance represents [Result.Loading].
 * Returns the original `Result` unchanged.
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}
