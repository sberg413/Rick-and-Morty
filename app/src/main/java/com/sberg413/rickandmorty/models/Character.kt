package com.sberg413.rickandmorty.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "characters")
@Parcelize
data class Character(
    @PrimaryKey
    val id: Int,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    @Embedded(prefix = "origin_")
    val origin: Origin,
    @Embedded(prefix = "location_")
    val location: CharacterLocation,
    val image: String,
    val name: String,
    val url: String,
    val created: String
) : Parcelable

@Parcelize
data class Origin(
    val name: String,
    val url: String
) : Parcelable

@Parcelize
data class CharacterLocation(
    val name: String,
    val url: String
) : Parcelable