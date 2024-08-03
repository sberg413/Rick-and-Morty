package com.sberg413.rickandmorty.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val characterId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)