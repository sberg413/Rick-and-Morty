package com.sberg413.rickandmorty

import com.sberg413.rickandmorty.data.remote.dto.CharacterDTO

object TestDto {

    val testCharacterDTO1 = CharacterDTO(
        created = "2017-11-04T18:48:46.250Z",
        episode = listOf("https://rickandmortyapi.com/api/episode/1", "https://rickandmortyapi.com/api/episode/2"),
        gender = "Male",
        id = 1,
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        location = CharacterDTO.Location(
            name = "Citadel of Ricks",
            url = "https://rickandmortyapi.com/api/location/3"
        ),
        name = "Rick Sanchez",
        origin = CharacterDTO.Origin(
            name = "Earth (C-137)",
            url = "https://rickandmortyapi.com/api/location/1"
        ),
        species = "Human",
        status = "Alive",
        type = "",
        url = "https://rickandmortyapi.com/api/character/1"
    )

    val testCharacterDTO2 = CharacterDTO(
        created = "2017-11-04T18:50:21.651Z",
        episode = listOf("https://rickandmortyapi.com/api/episode/1", "https://rickandmortyapi.com/api/episode/2"),
        gender = "Male",
        id = 2,
        image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
        location = CharacterDTO.Location(
            name = "Earth (Replacement Dimension)",
            url = "https://rickandmortyapi.com/api/location/20"
        ),
        name = "Morty Smith",
        origin = CharacterDTO.Origin(
            name = "unknown",
            url = ""
        ),
        species = "Human",
        status = "Alive",
        type = "",
        url = "https://rickandmortyapi.com/api/character/2"
    )
}