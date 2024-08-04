package com.sberg413.rickandmorty.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sberg413.rickandmorty.data.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.model.Location
import com.sberg413.rickandmorty.domain.usecase.GetCharacterDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CharacterDetailUiState {
    data object Loading : CharacterDetailUiState()
    data class Success(val character: Character, val location: Location?) : CharacterDetailUiState()
    data class Error(val message: String) : CharacterDetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Int = savedStateHandle.get<Int>(KEY_CHARACTER_ID) ?: -1

    private val _uiState = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = CharacterDetailUiState.Loading
            val result = getCharacterDetailUseCase.invoke(characterId)
            Log.d(TAG, "GetCharacterDetailUseCase result = $result")
            _uiState.value = when (result) {
                is ApiResult.Success -> CharacterDetailUiState.Success(
                    character = result.data.character,
                    location = result.data.location
                )
                is ApiResult.Error -> CharacterDetailUiState.Error(result.message)
                is ApiResult.Exception -> CharacterDetailUiState.Error(result.e.message ?: "An unknown error has occurred.")
            }
        }
    }

    companion object {
        private const val TAG = "DetailViewModel"
        const val KEY_CHARACTER_ID = "character"
    }
}
