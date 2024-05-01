package ru.glazunov.habitstracker.data.habits.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import ru.glazunov.habitstracker.data.habits.remote.models.Habit
import ru.glazunov.habitstracker.data.habits.remote.models.Uid

interface HabitsApi {
    @GET("habit")
    suspend fun getHabits(): Response<List<Habit>>

    @PUT("habit")
    suspend fun putHabit(@Body habit: Habit): Response<Uid>
}