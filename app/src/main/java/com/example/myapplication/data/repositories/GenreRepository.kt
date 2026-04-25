package com.example.myapplication.data.repositories

import com.example.myapplication.data.RetrofitInstance
import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.Libro

class GenreRepository {
    suspend fun getGenreList(): List<Genero>{
        try {
            val retroFitInstance = RetrofitInstance.api
            return retroFitInstance.listGenres()
        }catch (e: Exception){
            e.printStackTrace()
        }
        return emptyList()
    }

    suspend fun getGenreById(id: Int): Genero? {
        try {
            val retroFitInstance = RetrofitInstance.api
            return retroFitInstance.getGenreById(id)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

}
