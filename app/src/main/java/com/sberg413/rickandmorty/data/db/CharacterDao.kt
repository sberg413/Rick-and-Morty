package com.sberg413.rickandmorty.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sberg413.rickandmorty.data.api.dto.CharacterDTO

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterDTO>)

//    @Query("SELECT * FROM characters WHERE id = :id")
//    suspend fun getCharacterById(id: Int): CharacterDTO?

    @Query("SELECT * FROM characters WHERE name LIKE :name AND status LIKE :status")
    fun pagingSource(name: String, status: String): PagingSource<Int, CharacterDTO>

//    @Query("DELETE FROM characters WHERE id = :id")
//    suspend fun deleteCharacterById(id: Int)

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}