package ru.glazunov.habitstracker.data.habits.remote

import okhttp3.OkHttpClient
import okhttp3.internal.http.RetryAndFollowUpInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.glazunov.habitstracker.BuildConfig
import ru.glazunov.habitstracker.data.habits.remote.interceptors.AuthInterceptor
import java.util.concurrent.TimeUnit

class RemoteHabitsRepositoryBuilder {
    companion object {
        private const val BASE_URL = "https://droid-test-server.doubletapp.ru/api/"
        private const val API_KEY = BuildConfig.API_KEY
        private var instance: HabitsApi? = null

        fun getInstance(): HabitsApi {
            if (instance == null) {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(AuthInterceptor(API_KEY))
                    .followSslRedirects(true)
                    .build()
                val retrofit = Retrofit.Builder()
                    .client(
                        client.newBuilder()
                            .addInterceptor(RetryAndFollowUpInterceptor(client))
                            .build()
                    )
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                instance = retrofit.create(HabitsApi::class.java)
            }
            return instance!!
        }
    }
}