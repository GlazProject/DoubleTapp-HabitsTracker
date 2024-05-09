package ru.glazunov.habitstracker.data.habits.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import ru.glazunov.habitstracker.data.habits.remote.models.NetworkHabit
import ru.glazunov.habitstracker.data.habits.remote.models.Uid

interface HabitsApi {
    @GET("habit")
    suspend fun getHabits(): Response<List<NetworkHabit>>

    @PUT("habit")
    suspend fun putHabit(@Body habit: NetworkHabit): Response<Uid>

    @DELETE("habit")
    suspend fun deleteHabit(@Body uid: Uid)
}