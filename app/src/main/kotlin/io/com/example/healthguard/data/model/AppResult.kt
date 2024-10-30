
package io.com.example.healthguard.data.model

sealed class AppResult<out R> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error<T>(val message: String, val data: T? = null) : AppResult<T>()
    data class Loading<T>(val data: T? = null) : AppResult<T>()
}
