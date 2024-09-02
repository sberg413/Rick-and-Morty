package com.sberg413.rickandmorty.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sberg413.rickandmorty.ui.detail.CharacterDetailScreen
import com.sberg413.rickandmorty.ui.detail.DetailViewModel
import com.sberg413.rickandmorty.ui.main.MainCharacterListScreen
import com.sberg413.rickandmorty.ui.main.MainViewModel


@Composable
fun RickMortyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main_list"
    ) {
        composable("main_list") {
            val viewModel = hiltViewModel<MainViewModel>()
            MainCharacterListScreen(navController = navController, viewModel = viewModel )
        }
        // Define the route with a placeholder for the character ID
        composable(
            route = "character_detail/{character}",
            arguments = listOf(navArgument("character") {
                type = NavType.IntType
            })
        ) {
            val viewModel = hiltViewModel<DetailViewModel>()
            CharacterDetailScreen(navController = navController, viewModel = viewModel )
        }
    }
    
}