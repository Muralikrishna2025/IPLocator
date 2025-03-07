package uk.ac.tees.mad.iplocator.model.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.ac.tees.mad.iplocator.model.serviceapi.ipApiService
import uk.ac.tees.mad.iplocator.model.serviceapi.ipstackApiService

object IpApiRetrofitInstance {
    const val BASE_URL = "https://api.ipify.org"
    fun create(): ipApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        return retrofit.create(ipApiService::class.java)
    }
}