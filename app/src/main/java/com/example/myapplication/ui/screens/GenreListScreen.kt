package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.data.models.Genero
import com.example.myapplication.ui.NavScreens
import com.example.myapplication.ui.ViewModels.GenreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: GenreViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.deleteErrorMessage) {
        state.deleteErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            vm.clearDeleteError()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Géneros",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(NavScreens.GENRE_EDIT.name)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Crear género"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        GenreListContent(
            modifier = Modifier.padding(innerPadding),
            vm = vm
        )
    }
}

@Composable
fun GenreListContent(
    modifier: Modifier = Modifier,
    vm: GenreViewModel
) {
    val genreState by vm.state.collectAsState()
    val errorMessage = genreState.errorMessage

    if (genreState.isLoading) {
        LoadingGenresContent(modifier = modifier)
    } else if (errorMessage != null) {
        ErrorGenresContent(
            message = errorMessage,
            modifier = modifier,
            onRetry = {
                vm.clearError()
                vm.retryFetchGenres()
            }
        )
    } else if (genreState.genresList.isEmpty()) {
        EmptyGenresContent(
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = genreState.genresList,
                key = { genre -> genre.id }
            ) { genre ->
                GenreItem(
                    item = genre,
                    onClick = {
                        vm.selectItem(genre.id)
                    },
                    onDeleteClick = {
                        vm.requestDeleteGenre(genre)
                    }
                )
            }
        }
    }

    val pendingDeleteGenre = genreState.pendingDeleteGenre
    if (pendingDeleteGenre != null) {
        AlertDialog(
            onDismissRequest = {
                if (!genreState.isDeletingGenre) {
                    vm.cancelDeleteGenreRequest()
                }
            },
            title = {
                Text("Eliminar genero")
            },
            text = {
                Text("Estas seguro de eliminar '${pendingDeleteGenre.nombre}'?")
            },
            confirmButton = {
                TextButton(
                    enabled = !genreState.isDeletingGenre,
                    onClick = {
                        vm.confirmDeleteGenre()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !genreState.isDeletingGenre,
                    onClick = {
                        vm.cancelDeleteGenreRequest()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun GenreItem(
    item: Genero,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenreInitialCircle(
                genreName = item.nombre
            )

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar genero"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Toca para ver o editar este género",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun GenreInitialCircle(
    genreName: String
) {
    val initial = genreName
        .trim()
        .firstOrNull()
        ?.uppercaseChar()
        ?.toString()
        ?: "?"

    Surface(
        modifier = Modifier.size(52.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun EmptyGenresContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay géneros disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cuando se carguen géneros, aparecerán aquí.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadingGenresContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorGenresContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Error al cargar generos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

