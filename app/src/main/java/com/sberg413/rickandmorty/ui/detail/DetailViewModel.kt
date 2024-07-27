package com.sberg413.rickandmorty.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sberg413.rickandmorty.data.api.ApiResult
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.model.Location
import com.sberg413.rickandmorty.data.repository.LocationRepository
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
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _uiState: MutableStateFlow<CharacterDetailUiState> = MutableStateFlow(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "DetailViewModel"
        const val KEY_CHARACTER = "character"
    }

    init {
        viewModelScope.launch {
            savedStateHandle.get<Character>(KEY_CHARACTER)?.let { character ->
                // _uiState.value = _uiState.value.copy(character = character)
                getLocationFromCharacter(character)
            }
        }
    }

    private suspend fun getLocationFromCharacter(character: Character) {
        Log.d(TAG, "character = $character")
        val locationId = character.locationId?.takeIf { it.isNotEmpty() }
        if (locationId == null) {
            _uiState.value = CharacterDetailUiState.Success(
                character = character,
                location = null
            )
        } else {
            when (val result = locationRepository.getLocation(locationId)) {
                is ApiResult.Success -> {
                    _uiState.value = CharacterDetailUiState.Success(
                        character = character,
                        location = result.data
                    )
                }

                is ApiResult.Error -> {
                    _uiState.value = CharacterDetailUiState.Error(result.message)
                }

                is ApiResult.Exception -> {

                }
            }
        }

    }
}
