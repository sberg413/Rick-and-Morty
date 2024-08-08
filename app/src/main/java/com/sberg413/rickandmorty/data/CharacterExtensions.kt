package com.sberg413.rickandmorty.data

import com.sberg413.rickandmorty.data.local.entity.CharacterEntity
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.remote.dto.CharacterDTO

fun CharacterDTO.toCharacter(): Character {
    return Character(
        id,
        status,
        species,
        type,
        gender,
        origin.url.split("/").last(),
        location.url.split("/").last(),
        image,
        name)
}

fun CharacterEntity.toCharacter(): Character {
    return Character(
        id,
        status,
        species,
        type,
        gender,
        origin.url.split("/").last(),
        location.url.split("/").last(),
        image,
        name)
}

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