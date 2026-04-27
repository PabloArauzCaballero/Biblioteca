package com.example.myapplication.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.BookRepository
import com.example.myapplication.data.models.Libro
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
        _state.update { it.copy(isLoading = true, errorMessage = null) }
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
    }

    fun requestDelete(book: Libro) {
        _state.update {
            it.copy(
                pendingDeleteBook = book,
                errorMessage = null
            )
        }
    }

    fun cancelDeleteRequest() {
        _state.update {
            it.copy(
                pendingDeleteBook = null,
                isDeleting = false
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun confirmDeleteBook() = viewModelScope.launch {
        val bookToDelete = state.value.pendingDeleteBook ?: return@launch

        _state.update {
            it.copy(
                isDeleting = true,
                errorMessage = null
            )
        }

        val deleteResult = repository.deleteBook(bookToDelete.id)
        if (deleteResult.isSuccess) {
            val books = repository.getBookList()
            _state.update {
                it.copy(
                    bookList = books,
                    pendingDeleteBook = null,
                    isDeleting = false,
                    isLoading = false
                )
            }
        } else {
            _state.update {
                it.copy(
                    isDeleting = false,
                    pendingDeleteBook = null,
                    errorMessage = deleteResult.exceptionOrNull()?.message
                        ?: "No se pudo eliminar el libro"
                )
            }
        }
    }

}