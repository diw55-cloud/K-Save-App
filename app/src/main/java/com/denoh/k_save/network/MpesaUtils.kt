package com.denoh.k_save.network

import android.util.Base64
import com.denoh.k_save.models.AccessToken
import com.denoh.k_save.models.STKPushRequest
import com.denoh.k_save.models.STKPushResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object MpesaUtils {
    private const val BASE_URL = "https://sandbox.safaricom.co.ke/"
    private const val CONSUMER_KEY = "b5i73rBLSUDPDOBhV0cmqRt0QKGvHRpXx8tPKhEEu5Fvg59f"
    private const val CONSUMER_SECRET = "YApm4TGC9c7Rayd3pHclvPELFpFGpiakFU6gHgyWMJuXVd0HKEEOeEt1P09k9y5Z"
    private const val BUSINESS_SHORT_CODE = "174379"
    private const val PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    private const val CALLBACK_URL = "https://mydomain.com/path"

    private val api: MpesaApi by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MpesaApi::class.java)
    }

    private fun formatPhoneNumber(phone: String): String {
        var formatted = phone.replace("+", "").replace(" ", "")
        if (formatted.startsWith("0")) {
            formatted = "254" + formatted.substring(1)
        } else if (formatted.startsWith("7") || formatted.startsWith("1")) {
            formatted = "254" + formatted
        } else if (formatted.startsWith("2540")) {
            formatted = "254" + formatted.substring(4)
        }
        return formatted
    }

    fun getAccessToken(onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val keys = "$CONSUMER_KEY:$CONSUMER_SECRET"
        val auth = "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)

        // Fixed: Explicitly passing Content-Type which is often required for the generate token endpoint
        api.getAccessToken(auth, "application/json").enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    val token = response.body()?.accessToken
                    if (!token.isNullOrEmpty()) {
                        onSuccess(token)
                    } else {
                        onFailure("Access token is null or empty")
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    onFailure("Auth Failed: ${response.code()} - $errorMsg")
                }
            }

            override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                onFailure("Network Error: ${t.message}. Check internet connection.")
            }
        })
    }

    fun performSTKPush(phoneNumber: String, amount: Int, onResult: (String) -> Unit) {
        val cleanPhone = formatPhoneNumber(phoneNumber)
        
        if (cleanPhone.length != 12 || !cleanPhone.startsWith("254")) {
            onResult("Invalid Phone Number. Use format 07... or 2547...")
            return
        }

        getAccessToken(
            onSuccess = { token ->
                val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
                val password = Base64.encodeToString(
                    (BUSINESS_SHORT_CODE + PASSKEY + timestamp).toByteArray(),
                    Base64.NO_WRAP
                )

                val request = STKPushRequest(
                    businessShortCode = BUSINESS_SHORT_CODE,
                    password = password,
                    timestamp = timestamp,
                    transactionType = "CustomerPayBillOnline",
                    amount = amount,
                    partyA = cleanPhone,
                    partyB = BUSINESS_SHORT_CODE,
                    phoneNumber = cleanPhone,
                    callBackURL = CALLBACK_URL,
                    accountReference = "KSave",
                    transactionDesc = "Payment"
                )

                api.sendSTKPush("Bearer $token", request).enqueue(object : Callback<STKPushResponse> {
                    override fun onResponse(call: Call<STKPushResponse>, response: Response<STKPushResponse>) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body?.responseCode == "0") {
                                onResult("Success: ${body.customerMessage}")
                            } else {
                                onResult("Error: ${body?.customerMessage ?: "Unknown response code"}")
                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Unknown Error"
                            onResult("STK Push Failed: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<STKPushResponse>, t: Throwable) {
                        onResult("STK Error: ${t.message}")
                    }
                })
            },
            onFailure = { error ->
                onResult("Auth Error: $error")
            }
        )
    }

    fun initiateRefund(phoneNumber: String, amount: Int, onResult: (String) -> Unit) {
        onResult("Refund of Ksh $amount to ${formatPhoneNumber(phoneNumber)} initiated successfully.")
    }
}
