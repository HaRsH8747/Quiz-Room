package com.quizroom.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class AppPref(context: Context) {
    fun getInt(key: String?): Int {
        return appSharedPref.getInt(key, 0)
    }

    fun setInt(key: String?, value: Int) {
        prefEditor.putInt(key, value).apply()
    }

    fun clearInt(key: String?) {
        setInt(key, 0)
    }

    fun getString(key: String?): String? {
        return appSharedPref.getString(key, "")
    }

    fun setString(key: String?, value: String?) {
        prefEditor.putString(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return appSharedPref.getBoolean(key, false)
    }

    fun setBoolean(key: String?, value: Boolean) {
        prefEditor.putBoolean(key, value).apply()
    }

    fun clearString(key: String?) {
        setString(key, "")
    }

    companion object {
        const val QUIZ_ROOM_DATA = "QUIZ_ROOM_DATA"
        lateinit var appSharedPref: SharedPreferences
        lateinit var prefEditor: SharedPreferences.Editor
        const val HAS_WON = "HAS_WON"
        const val LEVEL_NUMBER = "LEVEL_NUMBER"
        const val USER_RANK = "USER_RANK"
        const val TOTAL_COINS = "TOTAL_COINS"
        const val TOTAL_QUESTIONS = "TOTAL_QUESTIONS"
        const val PLAYED_LEVELS = "PLAYED_LEVELS"
    }

    init {
        appSharedPref = context.getSharedPreferences(QUIZ_ROOM_DATA, Activity.MODE_PRIVATE)
        prefEditor = appSharedPref.edit()
    }
}
