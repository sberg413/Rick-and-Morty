package com.sberg413.rickandmorty.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.repository.CharacterRepository
import com.sberg413.rickandmorty.ui.NoSearchFilter
import com.sberg413.rickandmorty.ui.NoStatusFilter
import com.sberg413.rickandmorty.ui.SearchFilter
import com.sberg413.rickandmorty.ui.StatusFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MainUiState(
    val statusFilter: StatusFilter = NoStatusFilter,
    val searchFilter: SearchFilter = NoSearchFilter
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(private val characterRepository: CharacterRepository): ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _characterClicked = Channel<Character>(Channel.BUFFERED)
    val characterClicked = _characterClicked.receiveAsFlow()

    val listData: Flow<PagingData<Character>> = _uiState
        .flatMapLatest {
            Log.d(TAG, "Fetching character list with filter: $it")
            characterRepository.getCharacterList(
                it.searchFilter.search ?: "",
                it.statusFilter.status ?: ""
            )
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PagingData.empty()
        )

    fun setStatusFilter(status: String) {
        viewModelScope.launch {
            val value = if (status.endsWith("all", true))
                NoStatusFilter else StatusFilter(status)
            _uiState.update {
                it.copy(statusFilter = value)
            }
        }
    }

    fun setSearchFilter(search: String?) {
        viewModelScope.launch {
            val value = if (search.isNullOrBlank())
                NoSearchFilter else SearchFilter(search)
            _uiState.update {
                it.copy(searchFilter = value)
            }
        }
    }

    fun updateStateWithCharacterClicked(character: Character) {
        viewModelScope.launch {
            _characterClicked.send(character)
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}

