package com.sberg413.rickandmorty.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys", primaryKeys = ["name","status"])
data class RemoteKey(
    val name: String,
    val status: String,
    val prevKey: Int?,
    val nextKey: Int?
)