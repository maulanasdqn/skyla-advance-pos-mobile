package com.skyla.pos.network

import com.skyla.pos.common.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Resource<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Empty response body")
            } else {
                val errorBody = response.errorBody()?.string()
                val message = try {
                    // Try to parse error JSON
                    errorBody ?: "Unknown error"
                } catch (e: Exception) {
                    "Error: ${response.code()}"
                }
                Resource.Error(message, response.code())
            }
        } catch (e: IOException) {
            Timber.e(e, "Network error")
            Resource.Error("Network error. Please check your connection.")
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error")
            Resource.Error("Unexpected error: ${e.localizedMessage}")
        }
    }
}
