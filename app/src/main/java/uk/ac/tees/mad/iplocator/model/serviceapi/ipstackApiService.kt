package uk.ac.tees.mad.iplocator.model.serviceapi

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.ac.tees.mad.iplocator.model.dataclass.IpLocation

interface ipstackApiService {
    @GET("{ip}")
    suspend fun getIpLocation(
        @Path("ip") ip: String,
        @Query("access_key") accessKey: String
    ): IpLocation

    @GET("/")
    suspend fun getIpAddress(): String // Returns the IP address as a string
}