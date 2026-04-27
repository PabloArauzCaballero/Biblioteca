package com.example.myapplication.ui.ViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.Libro
import com.example.myapplication.data.models.LibroGeneroPivot
import com.example.myapplication.data.repositories.BookRepository
import com.example.myapplication.data.repositories.GenreRepository
import com.example.myapplication.ui.states.BookFormUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BooksFormViewModel: ViewModel() {
    private val _state: MutableStateFlow<BookFormUIModel> = MutableStateFlow(
        BookFormUIModel()
    )

    val state: StateFlow<BookFormUIModel> = _state.asStateFlow()
    val repository = BookRepository()
    val genreRepository = GenreRepository()

    init {
        fetchGenres()
    }

    fun enableEditMode() {
        _state.update {
            it.copy(
                isEditModeEnabled = true,
                errorMessage = null
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun saveBook(
        id: Int?,
        nombre: String,
        autor: String,
        editorial: String,
        imagen: String,
        sinopsis: String,
        isbn: String,
        calificacionText: String,
        generos: List<Genero>
    ) = viewModelScope.launch {
        val currentBook = _state.value.selectedBook
        val isCreating = currentBook == null
        val normalizedIsbn = isbn.trim()
        val normalizedImagen = imagen.trim()
        val calificacion = calificacionText.toIntOrNull()

        if (isCreating && normalizedIsbn.isBlank()) {
            _state.update { it.copy(errorMessage = "El ISBN es obligatorio al crear un libro.") }
            return@launch
        }

        if (isCreating && normalizedImagen.isBlank()) {
            _state.update { it.copy(errorMessage = "La URL de imagen es obligatoria al crear un libro.") }
            return@launch
        }

        if (calificacion == null || calificacion !in 0..10) {
            _state.update { it.copy(errorMessage = "La calificación debe estar entre 0 y 10.") }
            return@launch
        }

        if (!isCreating && currentBook.isbn != normalizedIsbn) {
            _state.update { it.copy(errorMessage = "El ISBN es inmutable y no se puede modificar.") }
            return@launch
        }

        _state.update {
            it.copy(
                loadingChangesCorrectly = true,
                changesSavedCorrectly = false,
                errorMessage = null
            )
        }

        val normalizedGenres = generos
            .distinctBy { it.id }
            .map { genre ->
                if (isCreating || id == null) {
                    genre
                } else {
                    genre.copy(
                        pivot = genre.pivot ?: LibroGeneroPivot(
                            libro_id = id,
                            genero_id = genre.id
                        )
                    )
                }
            }

        val bookToSave = Libro(
            id = if (isCreating) null else id,
            nombre = nombre.trim(),
            autor = autor.trim(),
            editorial = editorial.trim(),
            imagen = normalizedImagen,
            sinopsis = sinopsis.trim(),
            isbn = if (isCreating) normalizedIsbn else currentBook.isbn,
            calificacion = calificacion,
            generos = normalizedGenres
        )

        val result = if (isCreating) {
            repository.createBook(bookToSave)
        } else {
            repository.updateBook(id, bookToSave)
        }

        _state.update {
            it.copy(
                selectedBook = result ?: it.selectedBook,
                loadingChangesCorrectly = false,
                changesSavedCorrectly = result != null,
                isEditModeEnabled = false,
                errorMessage = if (result == null) "No se pudo guardar el libro." else null
            )
        }
    }

    fun addGenre(
        newGenre: Genero
    ){
        _state.update { currentState ->
            currentState.copy(
                selectedGenres = currentState.selectedGenres.orEmpty()
                    .plus(newGenre)
                    .distinctBy { it.id }
            )
        }
    }

    fun addAllGenre(
        newGenres: List<Genero>?
    ){
        _state.update { currentState ->
            currentState.copy(
                selectedGenres = (currentState.selectedGenres.orEmpty() + newGenres.orEmpty())
                    .distinctBy { it.id }
            )
        }
    }

    fun removeGenre(genre: Genero) {
        _state.update { currentState ->
            currentState.copy(
                selectedGenres = currentState.selectedGenres.orEmpty()
                    .filterNot { it.id == genre.id },
                selectedGenre = if (currentState.selectedGenre?.id == genre.id) null else currentState.selectedGenre
            )
        }
    }

    fun fetchGenres() = viewModelScope.launch {
        val result = genreRepository.getGenreList()
        _state.update {
            it.copy(
                genresList = result,
                isLoadingGenres = false,
            )
        }
    }

    fun selectGenre(genre: Genero) {
        _state.update { currentState ->
            currentState.copy(
                selectedGenre = genre
            )
        }
    }

    fun fetchBookItem(id: Int)= viewModelScope.launch{
        _state.update {
            it.copy(
                loadingChangesCorrectly = true,
                isEditModeEnabled = false,
                errorMessage = null
            )
        }
        val bookResult = repository.getBookById(id)
        _state.update{
            it.copy(
                selectedBook = bookResult,
                selectedGenres = bookResult?.generos.orEmpty(),
                loadingChangesCorrectly = false,
                errorMessage = if (bookResult == null) "No se pudo cargar el libro." else null
            )
        }
    }

    fun clearForm() {
        _state.update {
            it.copy(
                selectedBook = null,
                selectedGenres = emptyList(),
                selectedGenre = null,
                isEditModeEnabled = true,
                changesSavedCorrectly = false,
                loadingChangesCorrectly = false,
                errorMessage = null
            )
        }
    }

}
