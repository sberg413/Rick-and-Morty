package com.sberg413.rickandmorty.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sberg413.rickandmorty.data.local.dao.CharacterDao
import com.sberg413.rickandmorty.data.local.dao.RemoteKeyDao
import com.sberg413.rickandmorty.data.local.entity.CharacterEntity
import com.sberg413.rickandmorty.data.local.entity.EpisodeConverter
import com.sberg413.rickandmorty.data.local.entity.RemoteKey

@Database(entities = [CharacterEntity::class, RemoteKey::class], version = 1, exportSchema = false)
@TypeConverters(EpisodeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}
