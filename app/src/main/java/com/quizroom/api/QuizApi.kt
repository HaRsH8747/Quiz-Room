package com.quizroom.api

import com.quizroom.models.Categories
import com.quizroom.models.Levels
import com.quizroom.models.Questions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

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