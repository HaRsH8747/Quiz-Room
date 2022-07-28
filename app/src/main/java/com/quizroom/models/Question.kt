package com.quizroom.models

data class Question(
    val id: Int,
    val answers: List<Option>,
    val que_image: String,
    val question: String
)