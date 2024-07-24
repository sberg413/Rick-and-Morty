package com.sberg413.rickandmorty.ui.main

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.repository.CharacterRepository
import com.sberg413.rickandmorty.ui.CharacterFilter
import com.sberg413.rickandmorty.ui.NoSearchFilter
import com.sberg413.rickandmorty.ui.NoStatusFilter
import com.sberg413.rickandmorty.ui.SearchFilter
import com.sberg413.rickandmorty.ui.StatusFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MainUiState(
    val isLoading: Boolean = false,
    val currentCharacter: Character? = null,
    val characterFilter: CharacterFilter = CharacterFilter(NoStatusFilter, NoSearchFilter),
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(private val characterRepository: CharacterRepository): ViewModel() {

    val characterClicked: StateFlow<Character?> get() = _characterClicked
    private val _characterClicked = MutableStateFlow<Character?>(null)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val _characterFilterFlow =  MutableStateFlow(CharacterFilter(NoStatusFilter, NoSearchFilter))

    val listData: Flow<PagingData<Character>> = _characterFilterFlow
        .flatMapLatest {
            Log.d(TAG, "Fetching character list with filter: $it")
            characterRepository.getCharacterList(it.searchFilter.search, it.statusFilter.status)
                .catch { e ->
                    Log.e(TAG, "Error fetching character list,", e)
                }
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())

    fun setStatusFilter(status: String) {
        viewModelScope.launch {
            val value = if (status.endsWith("all", true))
                NoStatusFilter else StatusFilter(status)
            _characterFilterFlow.value = _characterFilterFlow.value.copy(statusFilter = value)
        }
    }

    fun setSearchFilter(search: String?) {
        viewModelScope.launch {
            val value = if (search.isNullOrBlank())
                NoSearchFilter else SearchFilter(search)
            _characterFilterFlow.value = _characterFilterFlow.value.copy(searchFilter = value)
        }
    }

    fun updateStateWithCharacterClicked(character: Character?) {
        viewModelScope.launch {
            _characterClicked.value = character
        }
    }

    fun getSelectedStatusIndex(options: Array<String>): Int {
        return _characterFilterFlow.value.statusFilter.status?.let {
            options.indexOf(it)
        } ?: -1
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}