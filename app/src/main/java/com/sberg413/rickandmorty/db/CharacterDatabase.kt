package com.sberg413.rickandmorty.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sberg413.rickandmorty.models.Character
import java.util.concurrent.Executors

@Database(
    entities = [Character::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class CharacterDatabase: RoomDatabase() {

    abstract fun charactersDao(): CharacterDAO
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        private const val TAG = "CharacterDatabase"
        private const val DB_NAME = "Characters.db"

        @Volatile
        private var INSTANCE: CharacterDatabase? = null

        fun getInstance(context: Context): CharacterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context = context.applicationContext,
                klass = CharacterDatabase::class.java,
                name = DB_NAME
            ).setQueryCallback(object: QueryCallback {
                override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
                    Log.v(TAG,"Query: $sqlQuery | Args: $bindArgs")
                }
            }, Executors.newSingleThreadExecutor())
                .build()
    }

}