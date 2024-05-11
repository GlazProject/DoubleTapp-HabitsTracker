package ru.glazunov.habitstracker.domain.repositories

import ru.glazunov.habitstracker.domain.models.Habit

interface IRemoteHabitsRepository {
    suspend fun getAll(): List<Habit>

    suspend fun put(habit: Habit): String?

    suspend fun delete(id: String): Boolean
}