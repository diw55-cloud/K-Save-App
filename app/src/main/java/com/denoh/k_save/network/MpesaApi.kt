package com.denoh.k_save.network

import com.denoh.k_save.models.AccessToken
import com.denoh.k_save.models.STKPushRequest
import com.denoh.k_save.models.STKPushResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MpesaApi {
    @GET("oauth/v1/generate?grant_type=client_credentials")
    fun getAccessToken(
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String = "application/json"
    ): Call<AccessToken>

    @POST("mpesa/stkpush/v1/processrequest")
    fun sendSTKPush(
        @Header("Authorization") auth: String,
        @Body request: STKPushRequest
    ): Call<STKPushResponse>
}
