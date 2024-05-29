package ru.glazunov.domain.di

import dagger.Module
import dagger.Provides
import ru.glazunov.domain.repositories.ILocalHabitsRepository
import ru.glazunov.domain.repositories.IRemoteHabitsRepository
import ru.glazunov.domain.repositories.ISyncHabitsRepository

@Module
class RepositoriesModule(
    private val syncHabitsRepository: ISyncHabitsRepository,
    private val localHabitsRepository: ILocalHabitsRepository,
    private val remoteHabitsRepository: IRemoteHabitsRepository) {

    @Provides
    @DomainScope
    fun sync(): ISyncHabitsRepository  = syncHabitsRepository

    @Provides
    @DomainScope
    fun local(): ILocalHabitsRepository = localHabitsRepository

    @Provides
    @DomainScope
    fun remote(): IRemoteHabitsRepository = remoteHabitsRepository
}