package uk.ac.tees.mad.iplocator.model.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.ac.tees.mad.iplocator.model.serviceapi.ipstackApiService

object IpstackRetrofitInstance {
    const val BASE_URL = "http://api.ipstack.com/"
    fun create(): ipstackApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()

        return retrofit.create(ipstackApiService::class.java)
    }
}