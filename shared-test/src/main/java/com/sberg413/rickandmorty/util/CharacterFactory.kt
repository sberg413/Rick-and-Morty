package com.sberg413.rickandmorty.util

import com.sberg413.rickandmorty.data.api.dto.CharacterListApi

class CharacterFactory {

    private var id = 0

    fun createMockCharacter(name: String) : CharacterListApi.Result {
        id++
        return CharacterListApi.Result(
            "1/1/2020",
            listOf("http://episodes.com/1", "http://episodes.com/2"),
            "male",
            id,
            "http://imageurl.com",
           CharacterListApi.Result.Location("someplace","http://someurl.com/"),
            "1",
            CharacterListApi.Result.Origin("someplace","http://someurl.com/"),
            "human",
            name,
            "",
            "http://someurl.com"
        )
    }
}