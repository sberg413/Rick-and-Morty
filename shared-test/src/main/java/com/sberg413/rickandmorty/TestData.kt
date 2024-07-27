package com.sberg413.rickandmorty

import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.model.Location

object TestData {

    val TEST_CHARACTER = Character(
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

    val TEST_CHARACTER_2 = Character(
        2,
        "Alive",
        "Human",
        "",
        "Male",
        "20",
        "20",
        "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
        "Morty Smith"
    )

    val TEST_CHARACTER_NO_LOCATION = Character(
        36,
        "Alive",
        "Alien",
        "",
        "Male",
        "20",
        "",
        "https://rickandmortyapi.com/api/character/avatar/36.jpeg",
        "Beta-Seven"
    )

    val TEST_LOCATION = Location(
        id = 20,
        name = "Earth (Replacement Dimension)",
        type = "Planet",
        dimension = "Replacement Dimension",
        residents = null,
        url = "https://rickandmortyapi.com/api/location/20",
        created = "2017-11-18T19:33:01.173Z"
    )

    fun readJsonFile(filename: String): String {
        return javaClass.classLoader!!.getResourceAsStream(filename)?.use { inputStream ->
            inputStream.bufferedReader().use { reader ->
                reader.readText()
            }
        } ?: ""
    }
}