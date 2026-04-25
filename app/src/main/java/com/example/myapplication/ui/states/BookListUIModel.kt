package com.example.myapplication.ui.states
import com.example.myapplication.data.models.Libro

data class BookListUIModel (
    val bookList: List<Libro>,
    val selectedBookId: Int? = null,
    val isOpenListGenres: Boolean? = false,
    val isLoading: Boolean? = true,
)