package com.example.myapplication.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        val result = repository.getGenreList()
        _state.update {
            it.copy(
                genresList = result,
                isLoading = false,
            )
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