package ru.glazunov.habitstracker.di

import android.app.Application
import dagger.Component
import ru.glazunov.data.habits.di.DataComponent
import ru.glazunov.domain.di.DomainComponent

@ApplicationScope
@Component(
    dependencies = [DataComponent::class, DomainComponent::class],
)
interface AppComponent {
    fun inject(app: Application)
}