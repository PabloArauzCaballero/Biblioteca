package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.myapplication.data.models.Genero
import com.example.myapplication.ui.NavScreens
import com.example.myapplication.ui.ViewModels.BooksFormViewModel
import com.example.myapplication.ui.ViewModels.BooksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFormScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    listBooksVM: BooksViewModel = viewModel(),
    formBooksVM: BooksFormViewModel = viewModel(),
) {
    Scaffold(
        topBar = {
            BookFormScreenHeader(
                navController = navController,
                listBooksVM = listBooksVM
            )
        },
        modifier = modifier
    ) { paddingValues ->
        BookFormScreenBody(
            navController = navController,
            formBooksVM = formBooksVM,
            listBooksVM = listBooksVM,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFormScreenHeader(
    navController: NavHostController,
    listBooksVM: BooksViewModel = viewModel(),
) {
    val selectedBook = listBooksVM.state.collectAsState().value.selectedBook

    val title = selectedBook?.nombre ?: "Registro Libro"

    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {
                        navController.navigate(NavScreens.BOOK_FORM.name)
                    }
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Nuevo libro",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = title,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreSelect(
    genres: List<Genero>?,
    selectedGenre: Genero?,
    onGenreSelected: (Genero) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val safeGenres = genres.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedGenre?.nombre ?: "Seleccione un género",
            onValueChange = {},
            readOnly = true,
            label = { Text("Género") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (safeGenres.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No hay géneros disponibles") },
                    onClick = {
                        expanded = false
                    }
                )
            } else {
                safeGenres.forEach { genre ->
                    DropdownMenuItem(
                        text = {
                            Text(genre.nombre)
                        },
                        onClick = {
                            onGenreSelected(genre)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookFormScreenBody(
    navController: NavHostController,
    formBooksVM: BooksFormViewModel = viewModel(),
    listBooksVM: BooksViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val selectedBook = listBooksVM.state.collectAsState().value.selectedBook
    val formInfo = formBooksVM.state.collectAsState().value

    var name by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var editorial by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var sinopsis by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("") }

    LaunchedEffect(selectedBook?.id) {
        name = selectedBook?.nombre.orEmpty()
        author = selectedBook?.autor.orEmpty()
        editorial = selectedBook?.editorial.orEmpty()
        imagen = selectedBook?.imagen.orEmpty()
        sinopsis = selectedBook?.sinopsis.orEmpty()
        calificacion = selectedBook?.calificacion?.toString().orEmpty()

        selectedBook?.generos?.let { genres ->
            formBooksVM.addAllGenre(genres)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Autor") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = editorial,
                onValueChange = { editorial = it },
                label = { Text("Editorial") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = imagen,
                onValueChange = { imagen = it },
                label = { Text("Imagen URL") },
                modifier = Modifier.weight(1f)
            )
        }

        if (imagen.isNotBlank()) {
            AsyncImage(
                model = imagen,
                contentDescription = "Vista previa de la imagen del libro",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Text("Pega un enlace de imagen para ver la vista previa.")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = sinopsis,
                onValueChange = { sinopsis = it },
                label = { Text("Sinopsis") },
                modifier = Modifier.weight(1f),
                minLines = 3
            )

            OutlinedTextField(
                value = calificacion,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        calificacion = newValue
                    }
                },
                label = { Text("Calificación") },
                modifier = Modifier.weight(1f)
            )
        }

        GenreSelect(
            genres = formInfo.genresList,
            selectedGenre = null,
            onGenreSelected = { genre ->
                formBooksVM.addGenre(genre)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Géneros seleccionados",
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(formInfo.selectedGenres.orEmpty()) { genre ->
                AssistChip(
                    onClick = {},
                    label = {
                        Text(genre.nombre)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}