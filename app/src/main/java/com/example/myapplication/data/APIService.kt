package com.example.myapplication.data

import com.example.myapplication.data.models.Genero
import com.example.myapplication.data.models.requests.CreateGenreRequest
import com.example.myapplication.data.models.requests.AsignGenreRequest
import com.example.myapplication.data.models.Libro
import retrofit2.http.Body
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("generos")
    suspend fun createGenre(
        @Body request: CreateGenreRequest
    ): Genero

    @DELETE("generos/{id}")
    suspend fun deleteGenre(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("libros")
    suspend fun createBook(
        @Body book: Libro,
    ): Libro

    @PUT("libros/{id}")
    suspend fun updateBook(
        @Path("id") id: Int?,
        @Body book: Libro,
    ): Libro

    @DELETE("libros/{id}")
    suspend fun deleteBook(
        @Path("id") id: Int?
    ): Response<Unit>

    @POST("libro-generos")
    suspend fun asignBook(
        @Body request: AsignGenreRequest
    ): Response<Unit>
}
