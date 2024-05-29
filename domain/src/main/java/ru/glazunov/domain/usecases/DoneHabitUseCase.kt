package ru.glazunov.domain.usecases

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.glazunov.domain.di.DomainScope
import ru.glazunov.domain.models.DoneHabitResult
import ru.glazunov.domain.models.DoneHabitResultType
import ru.glazunov.domain.models.HabitType
import ru.glazunov.domain.repositories.HabitsRepository
import java.time.LocalDate
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@DomainScope
class DoneHabitUseCase @Inject constructor(
    private val repository: HabitsRepository
){
    suspend fun done(id: UUID): DoneHabitResult {
        val habit = repository.get(id)
            ?: return DoneHabitResult(DoneHabitResultType.Error)

        val now = LocalDate.now()
        val previous = now.minusDays(habit.frequency.toLong() + 1 )
        val doneDatesLeft = habit.count - 1 - habit.doneDates.count { it.after(getDate(previous)) }

        withContext(IO){launch { repository.addDoneDate(getDate(now), id) }}

        if (habit.type == HabitType.POSITIVE)
            return DoneHabitResult(
                if (doneDatesLeft <= 0 ) DoneHabitResultType.Done else DoneHabitResultType.NeedMore,
                doneDatesLeft
            )

        return DoneHabitResult(
            if (doneDatesLeft <= 0) DoneHabitResultType.RunOut else DoneHabitResultType.NotMoreThen,
            doneDatesLeft
        )
    }

    private fun getDate(localDate: LocalDate): Date =
        Date(localDate.year, localDate.monthValue, localDate.dayOfMonth)
}