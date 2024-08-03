package com.sberg413.rickandmorty.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val created: String,
    val gender: String,
    val image: String,
    val name: String,
    val species: String,
    val status: String,
    val type: String,
    val url: String,
    @Embedded(prefix = "origin_") val origin: Origin,
    @Embedded(prefix = "location_") val location: Location,
    @ColumnInfo(name = "episode_list") val episode: List<String>
) {
    data class Origin(
        val name: String,
        val url: String
    )

    data class Location(
        val name: String,
        val url: String
    )
}