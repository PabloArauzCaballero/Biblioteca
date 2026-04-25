package com.example.myapplication.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.BookRepository
import com.example.myapplication.ui.states.BookListUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BooksViewModel: ViewModel() {
    private val _state: MutableStateFlow<BookListUIModel> = MutableStateFlow(
        BookListUIModel(
            bookList = emptyList()
        )
    )

    val state: StateFlow<BookListUIModel> = _state.asStateFlow()
    val repository = BookRepository()

    init {
        fetchBooks()
    }

    fun fetchBooks() = viewModelScope.launch {
        val result = repository.getBookList()
        _state.update {
            it.copy(
                bookList = result,
                isLoading = false,
            )
        }
    }

    fun selectItem(id: Int){
        _state.update {
            it.copy(
                selectedBookId = id
            )
        }
        fetchBookItem(id)
    }

    fun fetchBookItem(id: Int)= viewModelScope.launch{
        val bookResult = repository.getBookById(id)
        _state.update{
            it.copy(
                selectedBook = bookResult
            )
        }
    }
}