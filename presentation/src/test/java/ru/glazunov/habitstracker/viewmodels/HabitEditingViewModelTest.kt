package ru.glazunov.habitstracker.viewmodels

import android.arch.core.executor.testing.InstantTaskExecutorRule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ru.glazunov.domain.interactor.EditHabitInteractor
import ru.glazunov.domain.models.Habit
import ru.glazunov.domain.repositories.HabitsRepository
import ru.glazunov.domain.usecases.DeleteHabitUseCase
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class HabitEditingViewModelTest {

    @get:Rule
    val instantTaskRule: TestRule = InstantTaskExecutorRule()

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    private val savedHabit: Habit = Habit(title = "saved")
    private var pushedHabit: Habit? = null

    private lateinit var habitsRepository: HabitsRepository
    private lateinit var viewModel: HabitEditingViewModel

    private fun createViewModel() {
        viewModel = HabitEditingViewModel(
            EditHabitInteractor(habitsRepository),
            DeleteHabitUseCase(habitsRepository)
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        habitsRepository = mock()
        runBlocking {
            `when`(habitsRepository.get(savedHabit.id)).thenReturn(savedHabit)
            `when`(habitsRepository.get(any(UUID::class.java)))
        }

        createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get saved habit with id`() {
        viewModel.init()
        val habit = viewModel.getHabit(savedHabit.id).value
        assertNotNull(habit)
        assertEquals(savedHabit, habit)
    }
}