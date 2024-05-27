package ru.glazunov.data.habits.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().newBuilder().addHeader(AUTH_HEADER, apiKey).build())

    companion object {
        private const val AUTH_HEADER = "Authorization"
    }
}