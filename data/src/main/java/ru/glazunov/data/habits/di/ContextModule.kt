package ru.glazunov.data.habits.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private var context: Context) {
    @Provides
    @DataScope
    fun context(): Context {
        return context.applicationContext
    }
}