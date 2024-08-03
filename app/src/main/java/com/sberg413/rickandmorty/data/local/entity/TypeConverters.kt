package com.sberg413.rickandmorty.data.local.entity

import androidx.room.TypeConverter
import com.sberg413.rickandmorty.data.remote.dto.CharacterDTO

fun CharacterDTO.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        status = this.status,
        species = this.species,
        type = this.type,
        gender = this.gender,
        created = this.created,
        image = this.image,
        url = this.url,
        origin = CharacterEntity.Origin(this.origin.name, this.origin.url),
        location = CharacterEntity.Location(this.location.name, this.location.url),
        episode = this.episode // Ensure type converter is used
    )
}

class EpisodeConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}