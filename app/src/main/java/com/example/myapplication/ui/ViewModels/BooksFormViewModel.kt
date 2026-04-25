package com.example.myapplication.ui.ViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.Libro
import com.example.myapplication.data.repositories.BookRepository
import com.example.myapplication.data.repositories.GenreRepository
import com.example.myapplication.ui.states.BookFormUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BooksFormViewModel: ViewModel() {
        private val _state: MutableStateFlow<BookFormUiModel> = MutableStateFlow(
            BookFormUiModel()
    )

    val state: StateFlow<BookFormUiModel> = _state.asStateFlow()
    val repository = BookRepository()
    val genreRepository = GenreRepository()

    init {
        fetchGenres()
    }

    fun saveChangesBook (
        id: Int,
        nombre: String,
        autor: String,
        editorial: String,
        imagen: String,
        sinopsis: String,
        isbn: String,
        calificacion: Int,
        generos: List<Genero>
    )= viewModelScope.launch {
        repository.updateBook(
            id,
            Libro(
                id,
                nombre,
                autor,
                editorial,
                imagen,
                sinopsis,
                isbn,
                calificacion,
                generos
            )
        )
    }


    fun addGenre(
        newGenre: Genero
    ){
        _state.update { currentState ->
            currentState.copy(
                selectedGenres = currentState.selectedGenres?.plus(newGenre)
                    ?.distinctBy { it.id }
            )
        }
    }

    fun addAllGenre(
        newGenres: List<Genero>?
    ){
        _state.update { currentState ->
            currentState.copy(
                selectedGenres = currentState.selectedGenres.orEmpty() + newGenres.orEmpty()
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




}