package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.myapplication.data.models.Libro
import com.example.myapplication.ui.NavScreens
import com.example.myapplication.ui.ViewModels.BooksViewModel

@Composable
fun BookListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: BooksViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            vm.clearError()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            BookListScreenHeader(navController)
        }
    ) { innerPadding ->
        BookList(
            modifier = Modifier.padding(innerPadding),
            vm = vm,
            onNavigate = { bookId ->
                navController.navigate("${NavScreens.BOOK_DETAIL.name}/$bookId")
            }
        )
    }
}

@Composable
fun BookList(
    modifier: Modifier = Modifier,
    vm: BooksViewModel,
    onNavigate: (Int) -> Unit
) {
    val bookListState by vm.state.collectAsState()

    when {
        // Si esta cargando mostrar simbolo de cargando
        bookListState.isLoading -> {
            LoadingContent(modifier = modifier)
        }

        bookListState.fetchErrorMessage != null -> {
            ErrorBooksContent(
                message = bookListState.fetchErrorMessage!!,
                modifier = modifier,
                onRetry = {
                    vm.fetchBooks()
                }
            )
        }

        bookListState.bookList.isEmpty() -> {
            EmptyBooksContent(modifier = modifier)
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = bookListState.bookList,
                    key = { book -> book.id!! }
                ) { book ->
                    BookItem(
                        item = book,
                        onClick = {
                            book.id?.let(onNavigate)
                        },
                        onDeleteClick = {
                            vm.requestDelete(book)
                        }
                    )
                }
            }
        }
    }

    val pendingDeleteBook = bookListState.pendingDeleteBook
    if (pendingDeleteBook != null) {
        AlertDialog(
            onDismissRequest = {
                if (!bookListState.isDeleting) {
                    vm.cancelDeleteRequest()
                }
            },
            title = {
                Text("Eliminar libro")
            },
            text = {
                Text("Estas seguro de eliminar '${pendingDeleteBook.nombre}'?")
            },
            confirmButton = {
                TextButton(
                    enabled = !bookListState.isDeleting,
                    onClick = {
                        vm.confirmDeleteBook()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !bookListState.isDeleting,
                    onClick = {
                        vm.cancelDeleteRequest()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreenHeader(
    navController: NavHostController
){
    CenterAlignedTopAppBar(
        title = {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ){
                    IconButton(
                        modifier = Modifier.size(48.dp),
                        onClick = {
                            navController.navigate(NavScreens.BOOK_FORM.name)
                        }
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Biblioteca",
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            navController.navigate(NavScreens.HOME.name)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Ir al inicio",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
        }
    )
}

@Composable
fun BookItem(
    item: Libro,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    /*
        ElevatedCard para poner cartas con sombreado y
        que sean clickeable.
     */
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
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            BookCover(
                imageUrl = item.imagen,
                title = item.nombre,
                modifier = Modifier.width(86.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar libro"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.autor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.editorial,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.sinopsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                RatingAndIsbnRow(
                    calificacion = item.calificacion,
                    isbn = item.isbn
                )

                if (item.generos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    GenreList(
                        item = item
                    )
                }
            }
        }
    }
}

@Composable
fun BookCover(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    // Surfance es un contenedor con un contexto semantico: DAR SENSACION
    Surface(
        modifier = modifier
            .aspectRatio(0.69f)     // Mejor trabajar con aspect radio
            .clip(RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Portada de $title",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun RatingAndIsbnRow(
    calificacion: Int,
    isbn: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "★ $calificacion/10",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "ISBN: $isbn",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun GenreList(
    item: Libro
) {
    // FlowRow sirve para hacer wrapped rows (se va hacia abajo si no hay espacio)
    // Assit Chip se usa para mostrar una etiqueta secundaria

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item.generos.take(10).forEach { genero ->

            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = genero.nombre,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }

        if (item.generos.size > 10) {
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = "+${item.generos.size - 3}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }
}

@Composable
fun LoadingContent(
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
fun EmptyBooksContent(
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
                text = "No hay libros disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cuando se carguen libros, aparecerán aquí.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorBooksContent(
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
                text = "Error al cargar libros",
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
