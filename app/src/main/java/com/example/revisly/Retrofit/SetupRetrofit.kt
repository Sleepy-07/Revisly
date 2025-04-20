package com.example.revisly.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://universal-scraper-2.onrender.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ScraperApi = retrofit.create(ScraperApi::class.java)
}
