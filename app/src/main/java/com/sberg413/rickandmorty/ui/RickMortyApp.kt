@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.sberg413.rickandmorty.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sberg413.rickandmorty.ui.detail.CharacterDetailScreen
import com.sberg413.rickandmorty.ui.main.MainCharacterListScreen
import com.sberg413.rickandmorty.ui.theme.AppTheme


@OptIn(ExperimentalComposeUiApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun RickMortyApp() {
    AppTheme {
        val navController = rememberNavController()
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this
            ) {
                NavHost(
                    modifier = Modifier.semantics {
                        // Allows to use testTag() for UiAutomator resource-id.
                        testTagsAsResourceId = true
                    },
                    navController = navController,
                    startDestination = "main_list"
                ) {
                    composable("main_list") {
                        CompositionLocalProvider( LocalNavAnimatedVisibilityScope provides this) {
                            MainCharacterListScreen(
                                navController = navController
                            )
                        }
                    }
                    // Define the route with a placeholder for the character ID
                    composable(
                        route = "character_detail/{character}",
                        arguments = listOf(navArgument("character") {
                            type = NavType.IntType
                        })
                    ) {
                        CompositionLocalProvider( LocalNavAnimatedVisibilityScope provides this) {
                            CharacterDetailScreen(
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }