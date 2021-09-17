package ru.skillbranch.sbdelivery.data.network

import retrofit2.Response
import retrofit2.http.*
import ru.skillbranch.sbdelivery.data.network.req.ReviewReq
import ru.skillbranch.sbdelivery.data.network.res.*

interface RestService {

    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: RefreshToken): Token

    @GET("dishes")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun getDishes(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<DishRes>>

    @GET("dishes/{id}")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun getDish(
        @Path("id") id: String,
    ): Response<DishRes>

    @GET("main/recommend")
    suspend fun getRecommended(): Response<List<String>>

    @GET("categories")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun getCategories(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100
    ): List<CategoryRes>

    @GET("reviews/{dish}")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun getReviews(
        @Path("dish") dish:String,
        @Query("offset") offset: Int=0,
        @Query("limit") limit: Int=10
    ): Response<List<ReviewRes>>

    @POST("reviews/{dish}")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun sendReview(
        @Path("dish") dish:String,
        @Body review : ReviewReq,
        token: String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYxMGU5N2RmNGUzNDExMDAzY2M2ZTBlZCIsImlhdCI6MTYyODM0NjMzNSwiZXhwIjoxNjI4MzQ3NTM1fQ.JYVPAOxDOmmMJXULJD2MvMpXWdkMxnZ64KVrqP7SEns"
    ): ReviewRes

}