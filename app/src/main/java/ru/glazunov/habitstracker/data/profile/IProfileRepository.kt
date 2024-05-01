package ru.glazunov.habitstracker.data.profile

import android.graphics.Bitmap

interface IProfileRepository {
    suspend fun getProfileImage(): Bitmap?
}