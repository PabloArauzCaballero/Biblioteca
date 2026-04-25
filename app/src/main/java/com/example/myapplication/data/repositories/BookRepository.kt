package com.example.myapplication.data.repositories

import com.example.myapplication.data.RetrofitInstance
import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.Libro

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

    suspend fun getBookById(id: Int): Libro? {
        try{
            val retroFitInstance = RetrofitInstance.api
            return retroFitInstance.getBookById(id)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }


    suspend fun updateBook(id: Int, book: Libro): Libro? {
        try{
            val retroFitInstance = RetrofitInstance.api
            return retroFitInstance.updateBook(id, book)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

}
