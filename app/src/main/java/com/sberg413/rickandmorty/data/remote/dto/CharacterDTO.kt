package com.sberg413.rickandmorty.data.remote.dto

data class CharacterDTO(
    val created: String,
    val episode: List<String>,
    val gender: String,
    val id: Int,
    val image: String,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: String,
    val type: String,
    val url: String
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