package ru.glazunov.habitstracker.data.profile

import android.graphics.Bitmap
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ProfileApi {
    @GET
    @Streaming
    suspend fun getImage(@Url url: String): Response<Bitmap>
}