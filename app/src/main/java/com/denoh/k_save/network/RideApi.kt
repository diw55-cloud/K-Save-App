package com.denoh.k_save.network

import com.denoh.k_save.models.RideOption
import retrofit2.http.GET
import retrofit2.http.Query

interface RideApi {
    @GET("rides")
    suspend fun getRides(@Query("category") category: String): List<RideOption>

    @GET("categories")
    suspend fun getCategories(): List<String>
}
