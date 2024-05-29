package ru.glazunov.data.habits.di

import android.app.Application
import dagger.Component
import ru.glazunov.domain.repositories.ILocalHabitsRepository
import ru.glazunov.domain.repositories.IRemoteHabitsRepository
import ru.glazunov.domain.repositories.ISyncHabitsRepository

@DataScope
@Component(modules = [DataModule::class])
interface DataComponent {
    fun inject(app: Application)

    fun getLocalHabitsRepo(): ILocalHabitsRepository
    fun getSyncHabitsRepo(): ISyncHabitsRepository
    fun getRemoteHabitsRepo(): IRemoteHabitsRepository
}