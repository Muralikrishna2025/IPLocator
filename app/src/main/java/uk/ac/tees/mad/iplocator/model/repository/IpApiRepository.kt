package uk.ac.tees.mad.iplocator.model.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import uk.ac.tees.mad.iplocator.model.serviceapi.ipApiService
import java.io.IOException

class IpApiRepository(private val apiService: ipApiService) {
    suspend fun getIpAddress(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getIpAddress()
                Result.success(response)
            } catch (e: IOException) {
                // Handle network errors
                e.printStackTrace()
                Result.failure(Exception("Network error: ${e.message}"))
            } catch (e: HttpException) {
                // Handle HTTP errors (e.g., 404 Not Found, 500 Internal Server Error)
                e.printStackTrace()
                Result.failure(Exception("HTTP error: ${e.code()} - ${e.message}"))
            } catch (e: Exception) {
                // Handle other errors
                e.printStackTrace()
                Result.failure(Exception("An unexpected error occurred: ${e.message}"))
            }
        }
    }
}