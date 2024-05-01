package ru.glazunov.habitstracker.data.profile

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import ru.glazunov.habitstracker.data.profile.converters.BitmapConverterFactory

class ProfileRepository(private val api: ProfileApi): IProfileRepository {
    override suspend fun getProfileImage(): Bitmap? {
        val response: Response<Bitmap>? =
            try {
                withContext(IO) {
                    api.getImage(PROFILE_IMAGE_URL)
                }
            } catch (e: Exception) {
                Log.e("ProfileRepository", e.stackTraceToString())
                null
            }

        if (response?.isSuccessful != true)
            return null

        return response.body()
    }

    companion object{
        private const val BASE_URL = "https://gas-kvas.com/grafic/uploads/posts/2023-09/"
        private const val PROFILE_IMAGE_URL = "1695822868_gas-kvas-com-p-kartinki-milie-kotiki-13.jpg"
        private var instance: ProfileRepository? = null

        fun getInstance(): IProfileRepository {
            if (instance == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(BitmapConverterFactory.create()).build()
                instance = ProfileRepository(retrofit.create(ProfileApi::class.java))
            }
            return instance!!
        }
    }
}