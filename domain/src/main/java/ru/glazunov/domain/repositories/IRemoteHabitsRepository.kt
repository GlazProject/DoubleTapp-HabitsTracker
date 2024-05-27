package ru.glazunov.domain.repositories

import ru.glazunov.domain.models.Habit
import java.util.Date

interface IRemoteHabitsRepository {
    suspend fun getAll(): List<Habit>

    suspend fun put(habit: Habit): String?

    suspend fun delete(id: String): Boolean

    suspend fun done(date: Date, id: String): Boolean
}