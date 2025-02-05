package com.sberg413.rickandmorty.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sberg413.rickandmorty.data.remote.api.CharacterService
import com.sberg413.rickandmorty.data.remote.api.LocationService
import com.sberg413.rickandmorty.data.local.db.AppDatabase
import com.sberg413.rickandmorty.utils.ExcludeFromJacocoGeneratedReport
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.Executors
import javax.inject.Singleton

@ExcludeFromJacocoGeneratedReport
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"
        private const val DB_NAME = "rick_and_morty.db"
        private const val ENABLE_DB_LOGS = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build()

//    @Provides
//    @Singleton
//    fun providesGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val builder = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
        if (ENABLE_DB_LOGS) {
            builder.setQueryCallback(
                { sqlQuery, bindArgs ->
                    Log.v(
                        AppDatabase::class.simpleName,
                        "Query: $sqlQuery | Args: $bindArgs"
                    )
                },
                Executors.newSingleThreadExecutor()
            )
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideCharacterService(retrofit: Retrofit): CharacterService =
        retrofit.create(CharacterService::class.java)

    @Provides
    @Singleton
    fun provideLocationService(retrofit: Retrofit): LocationService =
        retrofit.create(LocationService::class.java)

    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}