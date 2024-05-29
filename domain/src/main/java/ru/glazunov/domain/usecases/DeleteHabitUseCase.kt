package ru.glazunov.domain.usecases

import ru.glazunov.domain.di.DomainScope
import ru.glazunov.domain.repositories.HabitsRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@DomainScope
class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitsRepository
){
    suspend fun delete(id: UUID) = repository.delete(id)
}