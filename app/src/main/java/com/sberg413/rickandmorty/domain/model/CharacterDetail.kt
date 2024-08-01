package com.sberg413.rickandmorty.domain.model

import androidx.compose.runtime.Immutable
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.model.Location

@Immutable
data class CharacterDetail (
    val character: Character,
    val location: Location?
)