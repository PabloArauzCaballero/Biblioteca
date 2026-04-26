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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.myapplication.data.models.Genero
import com.example.myapplication.ui.NavScreens
import com.example.myapplication.ui.ViewModels.BooksFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFormScreen(
    navController: NavHostController,
    bookId: Int?,
    modifier: Modifier = Modifier,
    formBooksVM: BooksFormViewModel = BooksFormViewModel()
) {
    LaunchedEffect(bookId) {
        if (bookId != null) {
            formBooksVM.fetchBookItem(bookId)
        } else {
            formBooksVM.clearForm()
        }
    }

    Scaffold(
        topBar = {
            BookFormScreenHeader(
                navController = navController,
                formBooksVM = formBooksVM
            )
        },
        modifier = modifier
    ) { paddingValues ->
        BookFormScreenBody(
            navController = navController,
            formBooksVM = formBooksVM,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFormScreenHeader(
    navController: NavHostController,
    formBooksVM: BooksFormViewModel = BooksFormViewModel()
) {
    val formInfo by formBooksVM.state.collectAsState()

    val title = if (formInfo.selectedBook != null) {
        formInfo.selectedBook!!.nombre.ifBlank { "Editar libro" }
    } else {
        "Registro Libro"
    }

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
                        navController.navigate(NavScreens.BOOKS_LIST.name)
                    }
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Nuevo libro",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {
                        navController.navigate(NavScreens.HOME.name)
                    }
                ) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "Home",
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
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
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
                    onClick = { expanded = false }
                )
            } else {
                safeGenres.forEach { genre ->
                    DropdownMenuItem(
                        text = { Text(genre.nombre) },
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
    formBooksVM: BooksFormViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val formInfo by formBooksVM.state.collectAsState()
    val selectedBook = formInfo.selectedBook

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
        ){
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

        }

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

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                onValueChange = { name = it },
                label = { Text("Editorial") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = imagen,
                onValueChange = { imagen = it },
                label = { Text("Imagen URL") },
                modifier = Modifier.weight(1f)
            )
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

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

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
            selectedGenre = formInfo.selectedGenre,
            onGenreSelected = { genre ->
                formBooksVM.selectGenre(genre)
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Button(
                onClick = {
                    when {
                        selectedBook == null -> {
                            //post
                        }

                        else -> {

                        }
                    }
                }
            ) { }
        }
    }
}