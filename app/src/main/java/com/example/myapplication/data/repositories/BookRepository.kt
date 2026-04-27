package com.example.myapplication.data.repositories

import com.example.myapplication.data.RetrofitInstance
import com.example.myapplication.data.models.Libro
import com.example.myapplication.data.models.requests.AsignGenreRequest

class BookRepository {
    suspend fun getBookList(): List<Libro>{
        try {
            val retroFitInstance = RetrofitInstance.api
            return retroFitInstance.listBooks()
        }catch (e: Exception){
            e.printStackTrace()
        }
        return emptyList()
    }

    suspend fun getBookListResult(): Result<List<Libro>> {
        return try {
            val retroFitInstance = RetrofitInstance.api
            Result.success(retroFitInstance.listBooks())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getBookById(id: Int): Libro? {
        try{
            val retroFitInstance = RetrofitInstance.api
            return retroFitInstance.getBookById(id)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

    suspend fun createBook(book: Libro): Libro? {
        try{
            val retroFitInstance = RetrofitInstance.api
            return retroFitInstance.createBook(book)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

    suspend fun updateBook(id: Int?, book: Libro): Libro? {
        try{
            val retroFitInstance = RetrofitInstance.api
            return retroFitInstance.updateBook(id, book)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

    suspend fun deleteBook(id: Int?): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.deleteBook(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar el libro (HTTP ${response.code()})"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun asignGenre(libroId: Int, generoId: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.asignBook(
                AsignGenreRequest(
                    libro_id = libroId,
                    genero_id = generoId
                )
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo asignar el género (HTTP ${response.code()})"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
    