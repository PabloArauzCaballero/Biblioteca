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

    fun clearSuccess() {
        _state.update { it.copy(successMessage = null) }
    }

    fun consumeSaveSuccess() {
        _state.update { it.copy(changesSavedCorrectly = false) }
    }

    fun consumeDeleteSuccess() {
        _state.update { it.copy(bookDeletedSuccessfully = false) }
    }

    fun deleteBook(id: Int?) = viewModelScope.launch {
        if (id == null) {
            _state.update { it.copy(errorMessage = "No se pudo identificar el libro a eliminar.") }
            return@launch
        }

        _state.update {
            it.copy(
                isDeletingBook = true,
                errorMessage = null,
                bookDeletedSuccessfully = false
            )
        }

        val result = repository.deleteBook(id)
        _state.update {
            it.copy(
                isDeletingBook = false,
                bookDeletedSuccessfully = result.isSuccess,
                selectedBook = if (result.isSuccess) null else it.selectedBook,
                selectedGenres = if (result.isSuccess) emptyList() else it.selectedGenres,
                selectedGenre = if (result.isSuccess) null else it.selectedGenre,
                errorMessage = if (result.isSuccess) null else result.exceptionOrNull()?.message
                    ?: "No se pudo eliminar el libro."
            )
        }
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
        if (_state.value.loadingChangesCorrectly) {
            return@launch
        }

        val currentBook = _state.value.selectedBook
        val isCreating = currentBook == null
        val normalizedName = nombre.trim()
        val normalizedAuthor = autor.trim()
        val normalizedEditorial = editorial.trim()
        val normalizedSinopsis = sinopsis.trim()
        val normalizedIsbn = isbn.trim()
        val isbnToValidate = if (isCreating) normalizedIsbn else currentBook?.isbn?.trim().orEmpty()
        val normalizedImagen = imagen.trim()
        val calificacion = calificacionText.toIntOrNull()
        val normalizedGenres = generos.distinctBy { it.id }

        if (normalizedName.isBlank() || normalizedAuthor.isBlank() || normalizedEditorial.isBlank()) {
            _state.update { it.copy(errorMessage = "Nombre, autor y editorial son obligatorios.", successMessage = null) }
            return@launch
        }

        if (normalizedSinopsis.isBlank()) {
            _state.update { it.copy(errorMessage = "La sinopsis es obligatoria.", successMessage = null) }
            return@launch
        }

        if (normalizedGenres.isEmpty()) {
            _state.update { it.copy(errorMessage = "Debes seleccionar al menos un genero.", successMessage = null) }
            return@launch
        }

        if (isbnToValidate.isBlank()) {
            _state.update { it.copy(errorMessage = "El ISBN es obligatorio.", successMessage = null) }
            return@launch
        }

        if (!isValidIsbnFormat(isbnToValidate)) {
            _state.update { it.copy(errorMessage = "El ISBN debe tener formato valido (10 o 13 digitos).", successMessage = null) }
            return@launch
        }

        if (normalizedImagen.isBlank()) {
            _state.update { it.copy(errorMessage = "La URL de imagen es obligatoria.", successMessage = null) }
            return@launch
        }

        if (normalizedImagen.isNotBlank() && !isValidHttpUrl(normalizedImagen)) {
            _state.update { it.copy(errorMessage = "La URL de imagen debe iniciar con http:// o https://.", successMessage = null) }
            return@launch
        }

        if (calificacion == null || calificacion !in 0..10) {
            _state.update { it.copy(errorMessage = "La calificación debe estar entre 0 y 10.", successMessage = null) }
            return@launch
        }

        if (!isCreating && currentBook.isbn != normalizedIsbn) {
            _state.update { it.copy(errorMessage = "El ISBN es inmutable y no se puede modificar.", successMessage = null) }
            return@launch
        }

        _state.update {
            it.copy(
                loadingChangesCorrectly = true,
                changesSavedCorrectly = false,
                errorMessage = null,
                successMessage = null
            )
        }

        val normalizedGenresWithPivot = normalizedGenres
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
            nombre = normalizedName,
            autor = normalizedAuthor,
            editorial = normalizedEditorial,
            imagen = normalizedImagen,
            sinopsis = normalizedSinopsis,
            isbn = if (isCreating) normalizedIsbn else currentBook.isbn,
            calificacion = calificacion,
            generos = normalizedGenresWithPivot
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
                bookDeletedSuccessfully = false,
                errorMessage = if (result == null) "No se pudo guardar el libro." else null,
                successMessage = if (result != null) {
                    if (isCreating) "Libro creado correctamente." else "Libro actualizado correctamente."
                } else {
                    null
                }
            )
        }
    }

    private fun isValidHttpUrl(value: String): Boolean {
        return try {
            val uri = java.net.URI(value)
            val scheme = uri.scheme?.lowercase()
            (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }

    private fun isValidIsbnFormat(value: String): Boolean {
        val normalized = value.replace("-", "").replace(" ", "")
        return normalized.all { it.isDigit() } && (normalized.length == 10 || normalized.length == 13)
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
                bookDeletedSuccessfully = false,
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
                bookDeletedSuccessfully = false,
                isDeletingBook = false,
                errorMessage = null,
                successMessage = null
            )
        }
    }

}
