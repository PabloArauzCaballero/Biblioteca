package com.example.myapplication.data

import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.Libro
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface APIService {
    @GET("libros")
    suspend fun listBooks(): List<Libro>

    @GET ("generos")
    suspend fun listGenres(): List<Genero>

    @GET("libros/{id}")
    suspend fun getBookById(
        @Path("id") id: Int
    ): Libro

    @GET("generos/{id}")
    suspend fun getGenreById(
        @Path("id") id: Int
    ): Genero

    @PUT("libros/{id}")
    suspend fun updateBook(
        @Path("id")id:Int,
        @Body book: Libro,
    ): Libro

}