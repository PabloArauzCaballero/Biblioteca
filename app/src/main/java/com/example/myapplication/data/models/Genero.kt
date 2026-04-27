package com.example.myapplication.data.models

data class Genero(
    val id: Int,
    val nombre: String,
    val pivot: LibroGeneroPivot? = null
)

data class LibroGeneroPivot(
    val libro_id: Int,
    val genero_id: Int
)
