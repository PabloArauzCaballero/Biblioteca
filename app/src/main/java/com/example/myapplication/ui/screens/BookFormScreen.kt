package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextOverflow
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
    formBooksVM: BooksFormViewModel
) {
    val formInfo by formBooksVM.state.collectAsState()

    val title = formInfo.selectedBook?.nombre?.ifBlank { "Editar libro" }
        ?: "Registro Libro"

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigate(NavScreens.BOOKS_LIST.name)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        actions = {
            if (formInfo.selectedBook != null && !formInfo.isEditModeEnabled) {
                IconButton(
                    onClick = { formBooksVM.enableEditMode() },
                    enabled = !formInfo.loadingChangesCorrectly
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Activar modo edición"
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val safeGenres = genres.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) expanded = !expanded
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedGenre?.nombre ?: "Seleccione un género",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
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
    val isCreating = selectedBook == null
    val formEnabled = isCreating || formInfo.isEditModeEnabled
    val fieldsEnabled = formEnabled && !formInfo.loadingChangesCorrectly

    var name by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var editorial by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var sinopsis by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("") }

    LaunchedEffect(selectedBook?.id) {
        name = selectedBook?.nombre.orEmpty()
        author = selectedBook?.autor.orEmpty()
        editorial = selectedBook?.editorial.orEmpty()
        imagen = selectedBook?.imagen.orEmpty()
        isbn = selectedBook?.isbn.orEmpty()
        sinopsis = selectedBook?.sinopsis.orEmpty()
        calificacion = selectedBook?.calificacion?.toString().orEmpty()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (formInfo.loadingChangesCorrectly) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("Guardando cambios...")
        }

        formInfo.errorMessage?.let { message ->
            Text(text = message)
        }

        if (!isCreating && !formInfo.isEditModeEnabled) {
            Text("Modo lectura. Presiona el botón de edición para modificar este libro.")
        }

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
                enabled = fieldsEnabled,
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
                enabled = fieldsEnabled,
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
                enabled = fieldsEnabled,
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
                enabled = fieldsEnabled,
                label = { Text(if (isCreating) "Imagen URL *" else "Imagen URL") },
                isError = isCreating && imagen.isBlank(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = isbn,
                onValueChange = { if (isCreating) isbn = it },
                enabled = isCreating && !formInfo.loadingChangesCorrectly,
                label = { Text(if (isCreating) "ISBN *" else "ISBN (inmutable)") },
                supportingText = {
                    if (!isCreating) Text("El ISBN no se puede modificar.")
                },
                isError = isCreating && isbn.isBlank(),
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
                enabled = fieldsEnabled,
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
                    if (newValue.all { it.isDigit() } && (newValue.toIntOrNull() ?: 0) <= 10) {
                        calificacion = newValue
                    }
                },
                enabled = fieldsEnabled,
                label = { Text("Calificación 0 - 10") },
                isError = calificacion.toIntOrNull()?.let { it !in 0..10 } ?: calificacion.isNotBlank(),
                modifier = Modifier.weight(1f)
            )
        }

        GenreSelect(
            genres = formInfo.genresList,
            selectedGenre = formInfo.selectedGenre,
            enabled = fieldsEnabled,
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
                    },
                    enabled = fieldsEnabled,
                    trailingIcon = {
                        if (fieldsEnabled) {
                            IconButton(
                                onClick = { formBooksVM.removeGenre(genre) },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Quitar género",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
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
                enabled = fieldsEnabled,
                onClick = {
                    formBooksVM.saveBook(
                        id = selectedBook?.id,
                        nombre = name,
                        autor = author,
                        editorial = editorial,
                        imagen = imagen,
                        sinopsis = sinopsis,
                        isbn = isbn,
                        calificacionText = calificacion,
                        generos = formInfo.selectedGenres.orEmpty(),
                    )
                }
            ) {
                Text(if (isCreating) "Crear" else "Guardar")
            }
        }
    }
}
