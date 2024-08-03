package com.sberg413.rickandmorty.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sberg413.rickandmorty.data.local.entity.CharacterEntity

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

//    @Query("SELECT * FROM characters WHERE name LIKE '%' || :name || '%' AND status LIKE '%' || :status || '%' LIMIT :loadSize OFFSET :offset")
//    suspend fun getCharactersByQuery(name: String, status: String, loadSize: Int, offset: Int): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :name || '%' AND status LIKE '%' || :status || '%'")
    fun getPagingSource(name: String, status: String): PagingSource<Int, CharacterEntity>

    @Query("DELETE FROM characters WHERE name LIKE '%' || :name || '%' AND status LIKE '%' || :status || '%'")
    suspend fun deleteByQuery(name: String, status: String)

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}