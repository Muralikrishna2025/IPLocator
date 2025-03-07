package uk.ac.tees.mad.iplocator.model.serviceapi

import retrofit2.http.GET

interface ipApiService {
    @GET("/")
    suspend fun getIpAddress(): String // Returns the IP address as a string
}