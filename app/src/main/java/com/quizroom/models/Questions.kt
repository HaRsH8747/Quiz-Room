package com.quizroom.models

data class Questions(
    val category: List<Question>,
    val timestamp: Int,
    val totalque: Int
)