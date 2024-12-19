package com.sberg413.rickandmorty

import com.sberg413.rickandmorty.data.model.Character


object PreviewData {



    val rick = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        originId = "3",
        locationId = "20",
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
    )

    val morty = Character(
        id = 2,
        name = "Morty Smith",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        originId = "1",
        locationId = "20",
        image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
    )

    val summer = Character(
        id = 3,
        name = "Summer Smith",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Female",
        originId = "1",
        locationId = "20",
        image = "https://rickandmortyapi.com/api/character/avatar/3.jpeg"
    )

    val beth = Character(
        4,
        "Alive",
        "Human",
        "",
        "Female",
        "20",
        "20",
        "https://rickandmortyapi.com/api/character/avatar/4.jpeg",
        "Beth Smith"
    )

    val jerry = Character(
        id = 5,
        name = "Jerry Smith",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        originId = "1",
        locationId = "20",
        image = "https://rickandmortyapi.com/api/character/avatar/5.jpeg"
    )

    val abradolf = Character(
        id = 6,
        name = "Abradolf Lincler",
        status = "unknown",
        species = "Human",
        type = "Genetic experiment",
        gender = "Male",
        originId = "3",
        locationId = "1",
        image = "https://rickandmortyapi.com/api/character/avatar/6.jpeg"
    )

    val birdperson = Character(
        id = 11,
        name = "Birdperson",
        status = "Alive",
        species = "Alien",
        type = "Bird-Person",
        gender = "Male",
        originId = "11",
        locationId = "11",
        image = "https://rickandmortyapi.com/api/character/avatar/11.jpeg"
    )

    val squanchy = Character(
        id = 22,
        name = "Squanchy",
        status = "unknown",
        species = "Alien",
        type = "Cat-person",
        gender = "Male",
        originId = "22",
        locationId = "22",
        image = "https://rickandmortyapi.com/api/character/avatar/22.jpeg"
    )

    val evilMorty = Character(
        id = 7,
        name = "Evil Morty",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        originId = "-1",
        locationId = "-1",
        image = "https://rickandmortyapi.com/api/character/avatar/7.jpeg"
    )

    val characterList = listOf(rick, morty, summer, beth, jerry, abradolf, birdperson, squanchy, evilMorty )
}