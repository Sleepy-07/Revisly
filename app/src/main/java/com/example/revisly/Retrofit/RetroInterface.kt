package com.example.revisly.Retrofit

import com.example.revisly.test
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ScraperApi {
    @POST("/scrape")
    fun sendUrl(@Body request: UrlRequest): Call<test>
}
