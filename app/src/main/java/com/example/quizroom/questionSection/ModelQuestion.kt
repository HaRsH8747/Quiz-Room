package com.example.quizroom.questionSection

data class ModelQuestion(
    val level: String,
    val question: String,
    val answer: Int,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String
)