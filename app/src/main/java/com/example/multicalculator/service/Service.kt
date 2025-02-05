package com.example.multicalculator.service

import com.example.multicalculator.Utils
import com.example.multicalculator.Utils.Companion.BASE_URL_CURRENCY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Service {
    @GET("v6/99dd4777c88bc3098b8654ca/pair/{from}/{to}")
    fun getExchangeRate(
        @Path("from") from: String,
        @Path("to") to: String
    ): Call<RetrofitInstance>
}