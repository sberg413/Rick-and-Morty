package com.sberg413.rickandmorty.ui.detail

import android.util.Log
import androidx.lifecycle.*
import com.sberg413.rickandmorty.models.Character
import com.sberg413.rickandmorty.models.Location
import com.sberg413.rickandmorty.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val characterData: LiveData<Character>
        get() = _characterData

    private val _characterData = MutableLiveData<Character>()
    val locationData: LiveData<Location>
        get() = _locationData

    init {
        _characterData.value = savedStateHandle.get<Character>("character")
    }

    private val _locationData = getLocation()

    private fun getLocation() : LiveData<Location>{
        return characterData.switchMap { character ->
            Log.d(TAG, "character = $character")
            val locationId = character.location.url.replace(Regex(".*/"), "")
            characterRepository.getLocation(locationId)
        }
    }

    companion object {
        private const val TAG = "DetailViewModel"
    }
}