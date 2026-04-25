package com.example.myapplication.data

import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.Libro
import retrofit2.http.GET

interface APIService {
    @GET("libros")
    suspend fun listBooks(): List<Libro>

    @GET ("generos")
    suspend fun listGenres(): List<Genero>

    @GET("libros/{id}")
    suspend fun getBookById(id: Int): Libro?

    @GET("generos/{id}")
    fun getGenreById(id: Int): Genero?
}