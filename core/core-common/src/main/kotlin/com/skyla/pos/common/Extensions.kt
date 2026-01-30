package com.skyla.pos.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Validates whether this string is a valid email address using a basic pattern check.
 *
 * @return true if the string matches a basic email pattern, false otherwise.
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return this.matches(emailRegex)
}

/**
 * Converts a [Collection] to a [kotlinx.collections.immutable]-style immutable list.
 * Returns a new list that is a snapshot of the current collection.
 */
fun <T> Collection<T>.toImmutableList(): List<T> {
    return this.toList()
}

/**
 * Returns a [Flow] that emits the first element and then ignores subsequent emissions
 * within the specified [windowDuration] in milliseconds.
 *
 * Useful for click debouncing / throttling in UI interactions.
 *
 * @param windowDuration The duration in milliseconds during which subsequent emissions are ignored.
 */
fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime >= windowDuration) {
            lastEmissionTime = currentTime
            emit(value)
        }
    }
}
