package com.sberg413.rickandmorty.data.local

import com.sberg413.rickandmorty.data.local.dao.CharacterDao
import com.sberg413.rickandmorty.data.local.entity.CharacterEntity
import javax.inject.Inject

class CharacterLocalDataSource @Inject constructor(
    private val characterDao: CharacterDao
) {

    suspend operator fun invoke(id: Int): CharacterEntity? {
        return characterDao.getCharacterById(id)
    }
}