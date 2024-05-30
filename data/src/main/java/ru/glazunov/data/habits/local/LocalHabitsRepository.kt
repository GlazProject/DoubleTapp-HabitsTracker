package ru.glazunov.data.habits.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.glazunov.data.habits.local.mapping.HabitMapping
import ru.glazunov.data.habits.local.providers.HabitDao
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.models.HabitType
import ru.glazunov.domain.models.Ordering
import ru.glazunov.domain.repositories.ILocalHabitsRepository
import java.util.Date
import java.util.UUID

class LocalHabitsRepository(private val dao: HabitDao)
    : ILocalHabitsRepository {
    override suspend fun delete(id: UUID) {
        dao.deleteHabit(id.toString())
    }

    override suspend fun put(habit: Habit) { dao.putHabit(HabitMapping.map(habit)) }

    override suspend fun get(id: UUID): Habit? {
        val data = dao.getHabit(id.toString()) ?: return null
        return HabitMapping.map(data)
    }

    override suspend fun addDoneDate(date: Date, id: UUID) {
        val data = dao.getHabit(id.toString())
        data?.doneDates?.add(date.time)
        if (data != null)
            dao.putHabit(data)
    }

    override fun getByTypeAndPrefix(
        habitType: HabitType,
        namePrefix: String,
        ordering: Ordering
    ): Flow<List<Habit>> {
        val flow = when(ordering){
            Ordering.Descending -> dao.getHabitsByTypeAndPrefixDesc(habitType.value, namePrefix)
            Ordering.Ascending -> dao.getHabitsByTypeAndPrefixAsc(habitType.value, namePrefix)
        }
        return flow.map{it.map{habit -> HabitMapping.map(habit)}}
    }
}