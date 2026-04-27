package com.example.myapplication.data.repositories

import com.example.myapplication.data.RetrofitInstance
import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.requests.CreateGenreRequest

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

    suspend fun getGenreListResult(): Result<List<Genero>> {
        return try {
            val retroFitInstance = RetrofitInstance.api
            Result.success(retroFitInstance.listGenres())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
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

    suspend fun createGenre(nombre: String): Result<Genero> {
        return try {
            val retroFitInstance = RetrofitInstance.api
            val createdGenre = retroFitInstance.createGenre(
                CreateGenreRequest(nombre = nombre)
            )
            Result.success(createdGenre)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteGenre(id: Int): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.deleteGenre(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar el genero (HTTP ${response.code()})"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

}
