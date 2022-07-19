package com.example.quizroom.api

import com.example.quizroom.models.Categories
import com.example.quizroom.models.Levels
import com.example.quizroom.models.Questions
import com.example.quizroom.utils.Constants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuizApi {

    @GET("v1/categories/{token}")
    fun getCategories(
        @Path("token")
        token: String
    ): Call<Categories>

    @GET("v1/levels/{catid}/{token}")
    fun getLevels(
        @Path("catid")
        catid: Int,
        @Path("token")
        token: String
    ): Call<Levels>

    @GET("v1/questions/{levelid}/{token}")
    fun getQuestions(
        @Path("levelid")
        levelid: Int,
        @Path("token")
        token: String
    ): Call<Questions>
}