package ru.skillbranch.sbdelivery.data.network.res

import com.squareup.moshi.Json
import java.io.Serializable

data class ReviewRes(
    @Json(name = "author")
    val name: String,
    @Json(name = "createdAt")
    val date: Long,
    val rating: Int,
    @Json(name = "text")
    val message: String
): Serializable