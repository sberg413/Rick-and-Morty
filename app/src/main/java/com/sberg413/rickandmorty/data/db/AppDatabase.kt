package com.sberg413.rickandmorty.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sberg413.rickandmorty.data.api.dto.CharacterDTO
import com.sberg413.rickandmorty.data.api.dto.Converters

@Database(entities = [CharacterDTO::class, RemoteKey::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}
