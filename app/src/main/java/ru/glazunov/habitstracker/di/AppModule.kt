package ru.glazunov.habitstracker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.internal.http.RetryAndFollowUpInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.glazunov.habitstracker.BuildConfig
import ru.glazunov.habitstracker.data.habits.local.HabitsDatabase
import ru.glazunov.habitstracker.data.habits.local.LocalHabitsRepository
import ru.glazunov.habitstracker.data.habits.local.providers.HabitDao
import ru.glazunov.habitstracker.data.habits.remote.HabitsApi
import ru.glazunov.habitstracker.data.habits.remote.RemoteHabitsRepository
import ru.glazunov.habitstracker.data.habits.remote.interceptors.AuthInterceptor
import ru.glazunov.habitstracker.data.habits.synchronization.HabitsSyncDatabase
import ru.glazunov.habitstracker.data.habits.synchronization.SyncHabitsRepository
import ru.glazunov.habitstracker.data.habits.synchronization.providers.HabitSyncDao
import ru.glazunov.habitstracker.domain.repositories.ILocalHabitsRepository
import ru.glazunov.habitstracker.domain.repositories.IRemoteHabitsRepository
import ru.glazunov.habitstracker.domain.repositories.ISyncHabitsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesBaseUrl(): String = "https://droid-test-server.doubletapp.ru/api/"

    @Singleton
    @Provides
    fun providesAuthInterceptor(): AuthInterceptor = AuthInterceptor(BuildConfig.API_KEY)

    @Singleton
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


    @Singleton
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
    @Singleton
    fun provideHabitsApi(retrofit: Retrofit): HabitsApi = retrofit.create(HabitsApi::class.java)

    @Provides
    @Singleton
    fun provideLocalHabitsDatabase(@ApplicationContext context: Context): HabitsDatabase = Room.databaseBuilder(
        context.applicationContext,
        HabitsDatabase::class.java,
        "HabitsDatabase"
    )
        .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideLocalHabitDao(database: HabitsDatabase): HabitDao = database.habitDao()

    @Provides
    @Singleton
    fun provideHabitsSyncDatabase(@ApplicationContext context: Context): HabitsSyncDatabase = Room.databaseBuilder(
        context.applicationContext,
        HabitsSyncDatabase::class.java,
        "HabitsSyncDatabase"
    )
        .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideHabitsSyncDao(database: HabitsSyncDatabase): HabitSyncDao = database.getDao()

    @Provides
    @Singleton
    fun providesRemoteHabitsRepo(api: HabitsApi): IRemoteHabitsRepository = RemoteHabitsRepository(api)

    @Provides
    @Singleton
    fun providesLocalHabitsRepo(dao: HabitDao): ILocalHabitsRepository = LocalHabitsRepository(dao)

    @Provides
    @Singleton
    fun providesSyncHabitsRepo(dao: HabitSyncDao): ISyncHabitsRepository = SyncHabitsRepository(dao)
}