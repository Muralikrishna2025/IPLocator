package uk.ac.tees.mad.iplocator.model.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.model.serviceapi.ipstackApiService
import java.io.IOException

class IpstackRepository(private val apiService: ipstackApiService) {
    private val ACCESS_KEY = "1ae3a020da57eae8651552b2ebb9cc3f"
        //"d74b8e4638543d6fb83ee1c830c3443a"

    suspend fun getIpLocationDetails(ip: String): Result<IpLocation> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getIpLocation(ip, ACCESS_KEY)
                // Assign a unique ID to each item based on its index in the list.
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