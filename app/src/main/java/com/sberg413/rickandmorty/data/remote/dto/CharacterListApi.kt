package com.sberg413.rickandmorty.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterListApi(
    val info: Info,
    val results: List<CharacterDTO>
) {
    data class Info(
        val count: Int,
        val next: String?,
        val pages: Int,
        val prev: String?
    )
}
