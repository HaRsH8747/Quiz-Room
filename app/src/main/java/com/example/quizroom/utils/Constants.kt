package com.example.quizroom.utils

import com.example.quizroom.models.Categories
import com.example.quizroom.models.Levels
import com.example.quizroom.models.Questions

class Constants {
    companion object{
        const val BASE_URL = "https://invisionicl.com/mobileapp/wp-json/quizroom/"
        lateinit var categoryResponse: Categories
        lateinit var levelsResponse: Levels
        lateinit var questionsResponse: Questions
        var currentLevelNo: Int = 1
        var currentCategory: Int = 1
        var currentLevelId: Int = 1
        var currentQuestionId: Int = 1
        var currentRightAnswers: Int = 0
        var isAudioEnabled: Boolean = true
    }
}