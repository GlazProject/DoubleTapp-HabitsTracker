package ru.glazunov.data.habits.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.internal.http.RetryAndFollowUpInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.glazunov.data.BuildConfig
import ru.glazunov.data.habits.local.HabitsDatabase
import ru.glazunov.data.habits.local.LocalHabitsRepository
import ru.glazunov.data.habits.local.providers.HabitDao
import ru.glazunov.data.habits.remote.RemoteHabitsRepository
import ru.glazunov.data.habits.remote.interceptors.AuthInterceptor
import ru.glazunov.data.habits.synchronization.HabitsSyncDatabase
import ru.glazunov.data.habits.synchronization.SyncHabitsRepository
import ru.glazunov.data.habits.synchronization.providers.HabitSyncDao
import ru.glazunov.domain.repositories.ILocalHabitsRepository
import ru.glazunov.domain.repositories.IRemoteHabitsRepository
import ru.glazunov.domain.repositories.ISyncHabitsRepository
import java.util.concurrent.TimeUnit

@Module(includes = [ContextModule::class])
object DataModule {
    @Provides
    @DataScope
    fun providesBaseUrl(): String = "https://droid-test-server.doubletapp.ru/api/"

    @DataScope
    @Provides
    fun providesAuthInterceptor(): AuthInterceptor =
        AuthInterceptor(BuildConfig.API_KEY)

    @DataScope
    @Provides
    fun providesOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .followSslRedirects(true)
        .build().let {it.newBuilder()
                .addInterceptor(RetryAndFollowUpInterceptor(it))
                .build()}


    @DataScope
    @Provides
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @Provides
    @DataScope
    fun provideHabitsApi(retrofit: Retrofit): ru.glazunov.data.habits.remote.HabitsApi =
        retrofit.create(
        ru.glazunov.data.habits.remote.HabitsApi::class.java)

    @Provides
    @DataScope
    fun provideLocalHabitsDatabase(context: Context): HabitsDatabase =
        Room.databaseBuilder(
        context.applicationContext,
        HabitsDatabase::class.java,
        "HabitsDatabase"
    )
        .allowMainThreadQueries()
        .build()

    @Provides
    @DataScope
    fun provideLocalHabitDao(database: HabitsDatabase): HabitDao = database.habitDao()

    @Provides
    @DataScope
    fun provideHabitsSyncDatabase(context: Context): HabitsSyncDatabase =
        Room.databaseBuilder(
        context.applicationContext,
        HabitsSyncDatabase::class.java,
        "HabitsSyncDatabase"
    )
        .allowMainThreadQueries()
        .build()

    @Provides
    @DataScope
    fun provideHabitsSyncDao(database: HabitsSyncDatabase): HabitSyncDao = database.getDao()

    @Provides
    @DataScope
    fun providesRemoteHabitsRepo(api: ru.glazunov.data.habits.remote.HabitsApi): IRemoteHabitsRepository =
        RemoteHabitsRepository(api)

    @Provides
    @DataScope
    fun providesLocalHabitsRepo(dao: HabitDao): ILocalHabitsRepository =
        LocalHabitsRepository(dao)

    @Provides
    @DataScope
    fun providesSyncHabitsRepo(dao: HabitSyncDao): ISyncHabitsRepository =
        SyncHabitsRepository(dao)
}