package ru.glazunov.habitstracker.data.habits.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT
import ru.glazunov.habitstracker.data.habits.remote.models.NetworkHabit
import ru.glazunov.habitstracker.data.habits.remote.models.Uid

interface HabitsApi {
    @GET("habit")
    suspend fun getHabits(): Response<List<NetworkHabit>>

    @PUT("habit")
    suspend fun putHabit(@Body habit: NetworkHabit): Response<Uid>

    @HTTP(method = "DELETE", path = "habit", hasBody = true)
    suspend fun deleteHabit(@Body uid: Uid): Response<Unit>
}