package com.sberg413.rickandmorty.data.local

import com.sberg413.rickandmorty.TestEntity
import com.sberg413.rickandmorty.data.local.dao.CharacterDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CharacterLocalDataSourceTest {

    @Mock
    private lateinit var characterDao: CharacterDao

    private lateinit var characterLocalDataSource: CharacterLocalDataSource

    @Before
    fun setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this)

        // Create an instance of CharacterLocalDataSource with the mocked CharacterDao
        characterLocalDataSource = CharacterLocalDataSource(characterDao)
    }

    @Test
    fun `invoke should return character when character is found`(): Unit = runBlocking {
        // Given
        val characterId = 1
        val characterEntity = TestEntity.testCharacterEntity
        `when`(characterDao.getCharacterById(characterId)).thenReturn(characterEntity)

        // When
        val result = characterLocalDataSource.invoke(characterId)

        // Then
        assertNotNull(result)
        assertEquals(characterEntity, result)
        verify(characterDao).getCharacterById(characterId)
    }

    @Test
    fun `invoke should return null when character is not found`(): Unit = runBlocking {
        // Given
        val characterId = 1
        `when`(characterDao.getCharacterById(characterId)).thenReturn(null)

        // When
        val result = characterLocalDataSource.invoke(characterId)

        // Then
        assertNull(result)
        verify(characterDao).getCharacterById(characterId)
    }
}