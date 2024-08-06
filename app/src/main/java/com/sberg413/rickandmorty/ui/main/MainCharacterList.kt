package com.sberg413.rickandmorty.ui.main

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.sberg413.rickandmorty.R
import com.sberg413.rickandmorty.data.model.Character
import com.sberg413.rickandmorty.ui.LoadingScreen


@Composable
fun MainCharacterListScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {

    val characters = viewModel.listData.collectAsLazyPagingItems()
    val (textState, setTextState) = rememberSaveable { mutableStateOf("") }
    val onSearch: (String) -> Unit = {
        viewModel.setSearchFilter(it)
    }
    val onItemClicked: (Character) -> Unit = {
        Log.d("MainCharacterListScreen", "Character clicked: $it")
        viewModel.updateStateWithCharacterClicked(it)
    }

    Column(modifier) {

        CharacterSearchInput(textState,
            modifier = modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            onValChange = setTextState,
            onSearch = onSearch
        )

        when (characters.loadState.refresh) {
            LoadState.Loading -> {
                LoadingScreen(modifier = modifier)
            }

            is LoadState.Error -> {
                ShowErrorLoadStateToast(characters.loadState)
            }

            else -> {
                if (characters.itemCount > 0) {
                    LazyColumn(modifier = modifier) {
                        items(count = characters.itemCount) { index ->
                            characters[index]?.let { item ->
                                CharacterListItem(
                                    character = item,
                                    modifier = modifier,
                                    clickListener = onItemClicked
                                )
                            }
                        }
                    }
                } else {
                    EmptyResultsView(modifier = modifier)
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CharacterListItem(character: Character, modifier: Modifier = Modifier, clickListener: (Character) -> Unit) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { clickListener(character) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            GlideImage(
                model = character.image,
                contentDescription = character.name,
                modifier = modifier.size(60.dp),
                loading = placeholder(R.drawable.avatar_placeholder)
            )

            Column(modifier = modifier.padding(start = 15.dp)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = modifier.padding(
                        top = 5.dp
                    )
                ) {
                    Text(
                        text = character.status,
                        modifier = modifier.padding(end = 10.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = character.species,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun CharacterListItemPreview() {
    val beth = Character(
        4,
        "Alive",
        "Human",
        "",
        "Female",
        "20",
        "20",
        "https://rickandmortyapi.com/api/character/avatar/4.jpeg",
        "Beth Smith"
    )
    MaterialTheme {
        CharacterListItem(character = beth, Modifier) {}
    }
}

@Composable
fun EmptyResultsView(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.no_results),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview(showBackground = true)
@Composable
fun EmptyResultsViewPreview() {
    EmptyResultsView()
}

@Composable
fun ShowErrorLoadStateToast(loadState: CombinedLoadStates) {
    val context = LocalContext.current
    val errorState = loadState.let {
        it.source.append as? LoadState.Error ?: it.source.prepend as? LoadState.Error
        ?: it.append as? LoadState.Error ?: it.prepend as? LoadState.Error
    }
    errorState?.let {
        Toast.makeText(
            context,
            "ERROR: ${it.error}",
            Toast.LENGTH_SHORT
        ).show()
    }
}