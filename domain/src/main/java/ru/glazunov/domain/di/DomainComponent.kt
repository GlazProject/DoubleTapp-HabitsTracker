package ru.glazunov.domain.di

import dagger.Component

@DomainScope
@Component(modules = [RepositoriesModule::class])
interface DomainComponent {
}