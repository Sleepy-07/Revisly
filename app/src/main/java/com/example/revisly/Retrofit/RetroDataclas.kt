package com.example.revisly.Retrofit

data class UrlRequest(
    val url: String
)

data class ScrapeResponse(
    val platform: String,
    val title: String?,
    val thumbnail: String?,
    val channel_name: String?, // or user_name for Instagram
    val video_type: String?,   // or post/reel/etc.
    val images: List<String>?, // for Instagram sidecar
    val error: String?         // optional for error catching
)
