package com.sberg413.rickandmorty.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sberg413.rickandmorty.ui.detail.CharacterDetailScreen
import com.sberg413.rickandmorty.ui.detail.DetailViewModel
import com.sberg413.rickandmorty.ui.main.MainCharacterListScreen
import com.sberg413.rickandmorty.ui.main.MainViewModel
import kotlinx.serialization.Serializable


sealed interface NavRoute {
    @Serializable
    object MainScreen

    @Serializable
    data class DetailScreen(val id: Int)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RickMortyApp() {

    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = NavRoute.MainScreen
        ) {
            composable<NavRoute.MainScreen> {
                val viewModel = hiltViewModel<MainViewModel>()
                MainCharacterListScreen(
                    navController = navController,
                    viewModel = viewModel,
                    animatedContentScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }
            // Define the route with a placeholder for the character ID
            composable<NavRoute.DetailScreen> {
                val viewModel = hiltViewModel<DetailViewModel>()
                CharacterDetailScreen(
                    navController = navController,
                    viewModel = viewModel,
                    animatedContentScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }
        }
    }
}
