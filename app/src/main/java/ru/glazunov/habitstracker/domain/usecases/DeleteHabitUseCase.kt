package ru.glazunov.habitstracker.domain.usecases

import ru.glazunov.habitstracker.domain.repositories.HabitsRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitsRepository
){
    suspend fun delete(id: UUID) = repository.delete(id)
}