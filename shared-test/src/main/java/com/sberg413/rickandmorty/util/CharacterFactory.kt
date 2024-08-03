package com.sberg413.rickandmorty.util

import com.sberg413.rickandmorty.data.remote.dto.CharacterDTO
import com.sberg413.rickandmorty.data.remote.dto.CharacterListApi

class CharacterFactory {

    private var id = 0

    fun createMockCharacter(name: String) : CharacterDTO {
        id++
        return CharacterDTO(
            "1/1/2020",
            listOf("http://episodes.com/1", "http://episodes.com/2"),
            "male",
            id,
            "http://imageurl.com",
           CharacterDTO.Location("someplace","http://someurl.com/"),
            "1",
            CharacterDTO.Origin("someplace","http://someurl.com/"),
            "human",
            name,
            "",
            "http://someurl.com"
        )
    }
}