package com.sberg413.rickandmorty.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.model.Location
import com.sberg413.rickandmorty.domain.GetCharacterDetailUseCase
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
    private val characterAndLocationUseCase: GetCharacterDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _uiState: MutableStateFlow<CharacterDetailUiState> = MutableStateFlow(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "DetailViewModel"
        const val KEY_CHARACTER_ID = "character"
    }

    init {
        viewModelScope.launch {
            savedStateHandle.get<Int>(KEY_CHARACTER_ID)?.let { id ->
                // _uiState.value = _uiState.value.copy(character = character)
                getCharacterDetailFromCharacterId(id)
            }
        }
    }

    private suspend fun getCharacterDetailFromCharacterId(characterId: Int) {
        Log.d(TAG, "character = $characterId")

        when (val result = characterAndLocationUseCase.invoke(characterId)) {
            is ApiResult.Success -> {
                _uiState.value = CharacterDetailUiState.Success(
                    character = result.data.character,
                    location = result.data.location
                )
            }

            is ApiResult.Error -> {
                _uiState.value = CharacterDetailUiState.Error(result.message)
            }

            is ApiResult.Exception -> {
                _uiState.value = CharacterDetailUiState.Error(result.e.message ?: "An unknown error has occurred.")
            }
        }
    }
}
