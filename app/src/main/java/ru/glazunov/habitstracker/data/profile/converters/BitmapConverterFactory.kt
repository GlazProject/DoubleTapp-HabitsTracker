package ru.glazunov.habitstracker.data.profile.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


class BitmapConverterFactory private constructor() : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? = if (type === Bitmap::class.java)
        Converter { value -> BitmapFactory.decodeStream(value.byteStream()) } else null

    companion object {
        fun create(): BitmapConverterFactory = BitmapConverterFactory()
    }
}