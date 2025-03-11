package uk.ac.tees.mad.iplocator.model.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation
import uk.ac.tees.mad.iplocator.model.serviceapi.ipstackApiService
import java.io.IOException

class IpstackRepository(private val apiService: ipstackApiService) {
    private val ACCESS_KEY = "03d2c74f011ff27228a11334c86102f5"
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