package com.udacity.asteroidradar.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PictureOfDay(
    val title: String,
    @Json(name = "media_type") val mediaType: String,
    val url: String
)