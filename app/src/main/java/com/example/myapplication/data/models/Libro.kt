package com.example.myapplication.data.models

data class Libro(
    val id: Int?,
    val nombre: String,
    val autor: String,
    val editorial: String,
    val imagen: String,
    val sinopsis: String,
    val isbn: String,
    val calificacion: Int,

    val generos: List<Genero>
)