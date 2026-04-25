package com.example.myapplication.ui.states

import com.example.myapplication.data.models.Genero

data class BookFormUiModel (
    val changesSavedCorrectly: Boolean = false,
    val loadingChangesCorrectly: Boolean = false,
    val genresList: List<Genero> ? = null,
    val isLoadingGenres: Boolean = true,
    val selectedGenres: List<Genero> ? = null,
    val selectedGenre: Genero ?= null
)
