package com.example.myapplication.ui.states

import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.Libro

data class BookFormUIModel (
    val changesSavedCorrectly: Boolean = false,
    val loadingChangesCorrectly: Boolean = false,
    val genresList: List<Genero> ? = null,
    val isLoadingGenres: Boolean = true,
    val selectedGenres: List<Genero> ? = null,
    val selectedGenre: Genero ?= null,
    val selectedBook: Libro ?=null
)
