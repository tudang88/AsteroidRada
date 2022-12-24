package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * MoshiConverter for get ImageOfDay
 */
private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

/**
 * Retrofit Api
 */

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

/**
 * Define the APIService interface base on HTTP protocol
 * this interface have a role similar to DAO for Room database
 */
interface AsteroidApiService {
    /**
     * get raw json string
     */
    @GET("neo/rest/v1/feed")
    suspend fun getProperties(
        @Query("start_date") startDay: String,
        @Query("end_date") endDay: String,
        @Query("api_key") api_key: String = Constants.API_KEY
    ): String

    /**
     * The below method will be used for obtaining the image of day
     * Deserialize to ImageOfDay for later use
     */
    @GET("planetary/apod")
    suspend fun getImageOfDay(@Query("api_key") api_key: String = Constants.API_KEY): PictureOfDay
}

/**
 * singleton for API
 */
object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}