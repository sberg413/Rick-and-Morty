@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sberg413.rickandmorty.ui.detail

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.sberg413.rickandmorty.R
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.data.model.Location
import com.sberg413.rickandmorty.ui.LoadingScreen
import com.sberg413.rickandmorty.ui.LocalNavAnimatedVisibilityScope
import com.sberg413.rickandmorty.ui.LocalSharedTransitionScope
import com.sberg413.rickandmorty.ui.theme.getTopAppColors
import com.sberg413.rickandmorty.utils.ExcludeFromJacocoGeneratedReport
import com.sberg413.rickandmorty.utils.findActivity


@Composable
fun CharacterDetailScreen(
    navController: NavController,
    viewModel: DetailViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedElementScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No SharedElementScope found")

    with(animatedVisibilityScope) {
        with(sharedTransitionScope) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text( stringResource(R.string.character_details) ) },
                        modifier = Modifier
                            .renderInSharedTransitionScopeOverlay(
                                zIndexInOverlay = 1f
                            )
                            .animateEnterExit(
                                enter = fadeIn() + slideInVertically {
                                    it
                                },
                                exit = fadeOut() + slideOutVertically {
                                    it
                                }
                            ),
                        colors = getTopAppColors().copy(
                            containerColor = Color.Transparent
                        ),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                    )
                }
            ) { innerPadding ->

                when (uiState) {
                    is CharacterDetailUiState.Loading -> {
                        LoadingScreen()
                    }

                    is CharacterDetailUiState.Success -> {
                        val character = (uiState as CharacterDetailUiState.Success).character
                        val location = (uiState as CharacterDetailUiState.Success).location

                        val context = LocalContext.current.findActivity()
                        LaunchedEffect(Unit) {
                            (context as? AppCompatActivity)?.supportActionBar?.title =
                                character.name
                        }

                        CharacterDetailContent(
                            modifier = Modifier.padding(innerPadding),
                            character = character,
                            locationData = location
                        )
                    }

                    is CharacterDetailUiState.Error -> {
                        ShowErrorStateToast((uiState as CharacterDetailUiState.Error).message)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CharacterDetailContent(
    modifier: Modifier = Modifier,
    character: Character,
    locationData: Location?
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedElementScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No SharedElementScope found")
    val colorStops = arrayOf(
        0.01f to MaterialTheme.colorScheme.primary,
        0.2F to MaterialTheme.colorScheme.secondaryContainer,
        1f to MaterialTheme.colorScheme.background,
    )
    val roundedCornerAnimation by animatedVisibilityScope.transition
        .animateDp(label = "rounded corner") { enterExit ->
            when (enterExit) {
                EnterExitState.PreEnter -> 0.dp
                EnterExitState.Visible -> 0.dp
                EnterExitState.PostExit -> 12.dp
            }
        }
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = colorStops,
                        tileMode = TileMode.Decal
                    )
                )
                .sharedBounds(
                    sharedTransitionScope.rememberSharedContentState(key = "border-${character.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransition = OverlayClip(
                        RoundedCornerShape(
                            roundedCornerAnimation
                        )
                    )
                )
        ) {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlideImage(
                    model = character.image,
                    contentDescription = stringResource(R.string.character_img_content_description, character.name),
                    modifier = Modifier
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "image-${character.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                spring(
                                    dampingRatio = 0.8f,
                                    stiffness = 385f
                                )
                            }
                        )
                        .width(260.dp)
                        .height(260.dp)
                        .padding(5.dp)
                        //.align(Alignment.Center)
                        .clip(RoundedCornerShape(12.dp)),
                    loading = placeholder(R.drawable.avatar_placeholder)
                )

                Text(
                    text = character.name,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 18.dp)
                        .testTag("CharacterName")
                        .sharedBounds(
                            sharedTransitionScope.rememberSharedContentState(key = "name-${character.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )


                // CharacterDetailRow(label = "Type", data = characterData.type)
                CharacterDetailRow(dataModifier = Modifier
                    .sharedBounds(
                        sharedTransitionScope.rememberSharedContentState(key = "status-${character.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                    label = R.string.status,
                    data = character.status)
                CharacterDetailRow(dataModifier = Modifier
                    .sharedBounds(
                        sharedTransitionScope.rememberSharedContentState(key = "species-${character.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                    label = R.string.species,
                    data = character.species)
                locationData?.let { location ->
                    CharacterDetailRow(label = R.string.location, data = location.name)
                    CharacterDetailRow(label = R.string.dimension, data = location.dimension)
                }
                // CharacterDetailRow(label = "Number of residents", data = locationData.residentCount.toString())

            }
        }
    }
}

@Composable
private fun CharacterDetailRow(modifier: Modifier =  Modifier, dataModifier: Modifier = Modifier, @StringRes label: Int, data: String ) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(0.47f),
            textAlign =  TextAlign.End
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 12.dp)
        )

        Text(
            text = data,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = dataModifier
                .testTag(stringResource(label)),
            textAlign =  TextAlign.Start
        )
    }

}

@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
private fun CharacterDetailContentPreview() {
    val character = Character(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "blah",
        gender = "Male",
        originId = "1",
        locationId = "1",
        image = "http://imageurl.com"
    )
    val location = Location(
        id = 1,
        name = "Earth",
        type = "Planet",
        dimension = "ea19",
        residents = null,
        url = "http://somelocation.com",
        created = "01/01/2023"

    )
    MaterialTheme {
        SharedTransitionLayout {
            CharacterDetailContent(
                character = character, locationData = location
            )
        }
    }
}

//@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
//@OptIn(ExperimentalGlideComposeApi::class)
//@Composable
//fun CharacterImage(url: String, name: String) {
//    Box(Modifier.fillMaxWidth()) {
//        GlideImage(
//            model = url,
//            contentDescription = stringResource(R.string.character_img_content_description, name),
//            modifier = Modifier
//                .width(200.dp)
//                .height(200.dp)
//                .padding(10.dp)
//                .align(Alignment.Center)
//                .clip(RoundedCornerShape(12.dp)),
//            loading = placeholder(R.drawable.avatar_placeholder)
//        )
//    }
//}

@Composable
fun ShowErrorStateToast(errMsg: String) {
    val context = LocalContext.current
    errMsg.let {
        Toast.makeText(
            context,
            "ERROR: $it",
            Toast.LENGTH_SHORT
        ).show()
    }
}
