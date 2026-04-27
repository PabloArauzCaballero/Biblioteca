package com.example.myapplication.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.repositories.GenreRepository
import com.example.myapplication.ui.states.GenreListUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenreViewModel: ViewModel() {
    private val _state: MutableStateFlow<GenreListUIModel> = MutableStateFlow(
        GenreListUIModel(
            genresList = emptyList()
        )
    )

    val state: StateFlow<GenreListUIModel> = _state.asStateFlow()
    val repository = GenreRepository()

    init {
        fetchGenres()
    }

    fun fetchGenres() = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        val result = repository.getGenreListResult()
        if (result.isSuccess) {
            _state.update {
                it.copy(
                    genresList = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessage = null
                )
            }
        } else {
            _state.update {
                it.copy(
                    genresList = emptyList(),
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "No se pudieron cargar los generos"
                )
            }
        }
    }

    fun retryFetchGenres() {
        fetchGenres()
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun onNewGenreNameChange(value: String) {
        _state.update {
            it.copy(
                newGenreName = value,
                newGenreNameError = null,
                submitErrorMessage = null,
                isCreateSuccess = false
            )
        }
    }

    fun resetCreateState() {
        _state.update {
            it.copy(
                newGenreName = "",
                newGenreNameError = null,
                submitErrorMessage = null,
                isSubmitting = false,
                isCreateSuccess = false
            )
        }
    }

    fun consumeCreateSuccess() {
        _state.update { it.copy(isCreateSuccess = false) }
    }

    fun requestDeleteGenre(genre: Genero) {
        _state.update {
            it.copy(
                pendingDeleteGenre = genre,
                deleteErrorMessage = null
            )
        }
    }

    fun cancelDeleteGenreRequest() {
        _state.update {
            it.copy(
                pendingDeleteGenre = null,
                isDeletingGenre = false
            )
        }
    }

    fun clearDeleteError() {
        _state.update { it.copy(deleteErrorMessage = null) }
    }

    fun confirmDeleteGenre() = viewModelScope.launch {
        val genreToDelete = state.value.pendingDeleteGenre ?: return@launch

        _state.update {
            it.copy(
                isDeletingGenre = true,
                deleteErrorMessage = null
            )
        }

        val result = repository.deleteGenre(genreToDelete.id)
        if (result.isSuccess) {
            _state.update {
                it.copy(
                    pendingDeleteGenre = null,
                    isDeletingGenre = false
                )
            }
            fetchGenres()
        } else {
            _state.update {
                it.copy(
                    pendingDeleteGenre = null,
                    isDeletingGenre = false,
                    deleteErrorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo eliminar el genero"
                )
            }
        }
    }

    fun createGenre() = viewModelScope.launch {
        val name = state.value.newGenreName.trim()
        if (name.isBlank()) {
            _state.update {
                it.copy(newGenreNameError = "El nombre del genero es obligatorio")
            }
            return@launch
        }

        _state.update {
            it.copy(
                isSubmitting = true,
                submitErrorMessage = null,
                newGenreNameError = null
            )
        }

        val result = repository.createGenre(name)
        if (result.isSuccess) {
            _state.update {
                it.copy(
                    isSubmitting = false,
                    isCreateSuccess = true,
                    newGenreName = ""
                )
            }
            fetchGenres()
        } else {
            _state.update {
                it.copy(
                    isSubmitting = false,
                    submitErrorMessage = result.exceptionOrNull()?.message
                        ?: "No se pudo crear el genero"
                )
            }
        }
    }

    fun selectItem(id: Int){
        _state.update {
            it.copy(
                selectedGenreId = id
            )
        }
        fetchBookItem(id)
    }

    fun fetchBookItem(id: Int)= viewModelScope.launch{
        val genreResult = repository.getGenreById(id)
        _state.update{
            it.copy(
                selectedGenre = genreResult
            )
        }
    }
}