package com.sberg413.rickandmorty

import com.sberg413.rickandmorty.data.local.entity.CharacterEntity

object TestEntity {

    val testCharacterEntity = CharacterEntity(
        id = 1,
        created = "2017-11-04T18:48:46.250Z",
        gender = "Male",
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        name = "Rick Sanchez",
        species = "Human",
        status = "Alive",
        type = "",
        url = "https://rickandmortyapi.com/api/character/1",
        origin = CharacterEntity.Origin(
            name = "Earth (C-137)",
            url = "https://rickandmortyapi.com/api/location/1"
        ),
        location = CharacterEntity.Location(
            name = "Citadel of Ricks",
            url = "https://rickandmortyapi.com/api/location/3"
        ),
        episode = listOf(
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2",
            "https://rickandmortyapi.com/api/episode/3"
        )
    )

}