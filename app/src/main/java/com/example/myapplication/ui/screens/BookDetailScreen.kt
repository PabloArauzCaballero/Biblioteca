package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.myapplication.ui.NavScreens
import com.example.myapplication.ui.ViewModels.BooksFormViewModel
import com.example.myapplication.ui.ViewModels.BooksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    navController: NavHostController,
    bookId: Int,
    formVM: BooksFormViewModel,
    listVM: BooksViewModel,
    modifier: Modifier = Modifier
) {
    val state by formVM.state.collectAsState()
    val selectedBook = state.selectedBook
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        formVM.fetchBookItem(bookId)
    }

    LaunchedEffect(state.bookDeletedSuccessfully) {
        if (state.bookDeletedSuccessfully) {
            formVM.consumeDeleteSuccess()
            listVM.fetchBooks()
            navController.popBackStack()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = selectedBook?.nombre ?: "Detalle del libro",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        enabled = selectedBook != null,
                        onClick = {
                            selectedBook?.id?.let { id ->
                                navController.navigate("${NavScreens.BOOK_FORM.name}/$id")
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar libro")
                    }
                    IconButton(
                        enabled = selectedBook != null && !state.isDeletingBook,
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar libro")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.loadingChangesCorrectly -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                }
            }

            selectedBook == null -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("No se pudo cargar el detalle del libro.")
                    Button(onClick = { formVM.fetchBookItem(bookId) }) {
                        Text("Reintentar")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedBook.imagen.isNotBlank()) {
                        AsyncImage(
                            model = selectedBook.imagen,
                            contentDescription = "Portada del libro",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )
                    }

                    Text("Nombre: ${selectedBook.nombre}", style = MaterialTheme.typography.titleMedium)
                    Text("Autor: ${selectedBook.autor}")
                    Text("Editorial: ${selectedBook.editorial}")
                    Text("ISBN: ${selectedBook.isbn}")
                    Text("Calificacion: ${selectedBook.calificacion}/10")
                    Text("Sinopsis:", fontWeight = FontWeight.Bold)
                    Text(selectedBook.sinopsis)

                    if (selectedBook.generos.isNotEmpty()) {
                        Text("Generos", fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(selectedBook.generos) { genero ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(genero.nombre) }
                                )
                            }
                        }
                    }

                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog && selectedBook != null) {
        AlertDialog(
            onDismissRequest = {
                if (!state.isDeletingBook) {
                    showDeleteDialog = false
                }
            },
            title = { Text("Eliminar libro") },
            text = { Text("Estas seguro de eliminar '${selectedBook.nombre}'?") },
            confirmButton = {
                TextButton(
                    enabled = !state.isDeletingBook,
                    onClick = {
                        formVM.deleteBook(selectedBook.id)
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !state.isDeletingBook,
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

