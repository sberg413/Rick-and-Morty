package com.sberg413.rickandmorty.di

import com.sberg413.rickandmorty.data.local.dao.CharacterDao
import com.sberg413.rickandmorty.data.local.db.AppDatabase
import com.sberg413.rickandmorty.data.repository.CharacterRepository
import com.sberg413.rickandmorty.data.repository.CharacterRepositoryImpl
import com.sberg413.rickandmorty.data.repository.LocationRepository
import com.sberg413.rickandmorty.data.repository.LocationRepositoryImpl
import com.sberg413.rickandmorty.utils.ExcludeFromJacocoGeneratedReport
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@ExcludeFromJacocoGeneratedReport
@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule
{

    @Binds
    abstract fun bindCharacterRepository(
        characterRepositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    abstract fun bindLocationRepository(
        characterRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    companion object {

        @Provides
        fun providesCharacterDao(appDatabase: AppDatabase): CharacterDao {
            return appDatabase.characterDao()
        }
    }

}