package com.example.myapplication.ui.states

import com.example.myapplication.data.models.Genero

data class GenreListUIModel (
    val genresList: List<Genero>,
    val selectedGenreId:Int?=null,
    val selectedGenre: Genero? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val newGenreName: String = "",
    val newGenreNameError: String? = null,
    val isSubmitting: Boolean = false,
    val submitErrorMessage: String? = null,
    val isCreateSuccess: Boolean = false,
    val pendingDeleteGenre: Genero? = null,
    val isDeletingGenre: Boolean = false,
    val deleteErrorMessage: String? = null
)