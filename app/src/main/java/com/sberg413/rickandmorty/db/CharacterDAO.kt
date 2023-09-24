package com.sberg413.rickandmorty.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sberg413.rickandmorty.models.Character

@Dao
interface CharacterDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<Character>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(characters: Character)

    @Query("SELECT * FROM characters " +
            "WHERE (:queryString IS NULL OR name LIKE :queryString) " +
            "AND (:status IS NULL OR status = :status) " +
            "ORDER BY id ASC")
    fun charactersByName(queryString: String?, status: String?): PagingSource<Int, Character>

    @Query("DELETE FROM characters")
    suspend fun clearCharacters()
}