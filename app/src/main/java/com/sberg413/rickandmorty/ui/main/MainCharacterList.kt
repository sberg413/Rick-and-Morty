@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sberg413.rickandmorty.ui.main

import android.util.Log
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.sberg413.rickandmorty.utils.PreviewData
import com.sberg413.rickandmorty.R
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.ui.LoadingScreen
import com.sberg413.rickandmorty.ui.LocalNavAnimatedVisibilityScope
import com.sberg413.rickandmorty.ui.LocalSharedTransitionScope
import com.sberg413.rickandmorty.ui.theme.getTopAppColors
import com.sberg413.rickandmorty.utils.ExcludeFromJacocoGeneratedReport
import com.sberg413.rickandmorty.utils.RMPreviewWrapper
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf


@Composable
fun MainCharacterListScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val characters = viewModel.listData.collectAsLazyPagingItems()


    val onSearch: (String) -> Unit = {
        viewModel.setSearchFilter(it)
    }
    val onStatusSelection: (String) -> Unit = {
        viewModel.setStatusFilter(it)
    }
    val onItemClicked: (Character) -> Unit = {
        Log.d("MainCharacterListScreen", "Character clicked: $it")
        viewModel.updateStateWithCharacterClicked(it)
    }

    LaunchedEffect(Unit) {
        viewModel.characterClicked
            .filterNotNull()
            .collect { character ->
                Log.d("MainCharacterListScreen", "characterClicked: $character")
                navController.navigate("character_detail/${character.id}")
            }
    }

    MainCharacterListContent(uiState, characters, onItemClicked, onSearch, onStatusSelection)
}

@Composable
fun MainCharacterListContent(
    uiState: MainUiState,
    characters: LazyPagingItems<Character>,
    onItemClicked: (Character) -> Unit,
    onSearch: (String) -> Unit,
    onStatusSelection: (String) -> Unit
) {

    val (textState, setTextState) = rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag("TopAppBar"),
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = getTopAppColors(),
                actions = {
                    StatusDropdownMenu(
                        options = stringArrayResource(id = R.array.filter_options).asList(),
                        selectedOption = uiState.statusFilter.status,
                        expanded = expanded,
                        onExpandChange = { expanded = it },
                        onSelection = onStatusSelection
                    )
                }
            )

        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            CharacterSearchInput(
                textState,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .testTag("CharacterSearchInput"),
                onValChange = setTextState,
                onSearch = onSearch
            )

            CharacterResultContent(
                characters = characters,
                onItemClicked = onItemClicked,
                snackbarHostState = snackbarHostState
            )
        }

    }
}

@Composable
fun StatusDropdownMenu(
    options: List<String>,
    selectedOption: String?,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onSelection: (String) -> Unit
) {
    IconButton(onClick = { onExpandChange(!expanded) }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More"
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            options.forEachIndexed { i, option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    trailingIcon = {
                       if (selectedOption == option || (selectedOption == null && i == 0 )) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected"
                            )
                       }
                    },
                    onClick = {
                        onExpandChange(false)
                        Log.d("StatusDropdownMenu", "Status selected: $option")
                        onSelection(option)
                    })
            }
        }
    }
}

@Composable
fun CharacterResultContent(
    modifier: Modifier = Modifier,
    characters: LazyPagingItems<Character>,
    onItemClicked: (Character) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    when (val loadState = characters.loadState.refresh) {
        LoadState.Loading -> LoadingScreen(modifier = modifier)
        is LoadState.Error -> {
            // Display the error snackbar using LaunchedEffect directly on loadState
            LaunchedEffect(loadState) {
                snackbarHostState.showSnackbar(
                    message = "ERROR: ${loadState.error.localizedMessage ?: "Unknown error"}",
                    duration = SnackbarDuration.Short
                )
            }
            EmptyResultsView(modifier = modifier) // Optional: Show empty view in case of error
        }
        else -> {
            if (characters.itemCount > 0) {
                CharacterGridResults(modifier, characters, onItemClicked)
            } else {
                EmptyResultsView(modifier = modifier)
            }
        }
    }
}

@Composable
fun CharacterGridResults(
    modifier: Modifier = Modifier,
    characters: LazyPagingItems<Character>,
    onItemClicked: (Character) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.testTag("CharacterList"),
        columns = GridCells.Adaptive(280.dp)
    ) {
        items(count = characters.itemCount) { index ->
            characters[index]?.let { item ->
                CharacterListItem(
                    character = item,
                    clickListener = onItemClicked
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CharacterListItem(
    character: Character,
    modifier: Modifier = Modifier,
    clickListener: (Character) -> Unit,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedElementScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No SharedElementScope found")


    with(sharedTransitionScope) {
        val roundedCornerAnimation by animatedVisibilityScope.transition
            .animateDp(label = "rounded corner") { enterExit ->
                when (enterExit) {
                    EnterExitState.PreEnter -> 12.dp
                    EnterExitState.Visible -> 12.dp
                    EnterExitState.PostExit -> 0.dp
                }
            }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clickable { clickListener(character) }
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .shadow(4.dp, MaterialTheme.shapes.medium) // Apply elevation
                .clip(MaterialTheme.shapes.medium) // Apply shape for clipping
                .border(0.5.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.medium)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.shapes.medium
                ) // Apply background and shape
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
            Row(modifier = Modifier.padding(12.dp)) {
                GlideImage(
                    model = character.image,
                    contentDescription = character.name,
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
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    loading = placeholder(R.drawable.avatar_placeholder)
                )

                Column(modifier = Modifier
                    .padding(start = 22.dp)
                    .align(Alignment.CenterVertically)) {
                    Text(
                        modifier = Modifier
                            .sharedBounds(
                                sharedTransitionScope.rememberSharedContentState(key = "name-${character.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                        text = character.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.padding(
                            top = 5.dp
                        )
                    ) {
                        Text(
                            text = character.status,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .sharedBounds(
                                    sharedTransitionScope.rememberSharedContentState(key = "status-${character.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = character.species,
                            modifier = Modifier
                                .sharedBounds(
                                    sharedTransitionScope.rememberSharedContentState(key = "species-${character.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyResultsView(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .testTag("EmptyResultsView")
    ) {
        Text(
            text = stringResource(id = R.string.no_results),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
fun CharacterListItemPreview() {
    RMPreviewWrapper {
        CharacterListItem(
            character = PreviewData.beth,
            Modifier
        ) {}
    }
}

@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
fun MainCharacterListContentPreview() {
    val uiState = MainUiState()
         
    val characters = flowOf(
        PagingData.from(
            PreviewData.characterList,
            LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true)
            )
        )
    ).collectAsLazyPagingItems()

    RMPreviewWrapper {
        MainCharacterListContent(uiState, characters, {}, {}, {})
    }
}

@ExcludeFromJacocoGeneratedReport
@Preview(showBackground = true)
@Composable
fun EmptyResultsViewPreview() {
    EmptyResultsView()
}


