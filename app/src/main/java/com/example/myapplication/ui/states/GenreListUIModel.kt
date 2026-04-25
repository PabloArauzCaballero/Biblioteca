package com.example.myapplication.ui.states

import com.example.myapplication.data.models.Genero

data class GenreListUIModel (
    val genresList: List<Genero>,
    val selectedGenreId:Int?=null,
    val selectedGenre: Genero? = null,
    val isLoading: Boolean? = true,
    )