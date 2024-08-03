package com.sberg413.rickandmorty.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sberg413.rickandmorty.data.local.entity.RemoteKey


@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE name = :name AND status = :status")
    suspend fun remoteKeyByQuery(name: String, status: String): RemoteKey

    @Query("DELETE FROM remote_keys WHERE name = :name AND status = :status")
    suspend fun deleteByQuery(name: String, status: String)
}