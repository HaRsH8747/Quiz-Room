package com.quizroom.levelSection

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.quizroom.R
import com.quizroom.api.RetrofitInstance
import com.quizroom.models.Level
import com.quizroom.models.Questions
import com.quizroom.utils.AppPref
import com.quizroom.utils.Constants.Companion.currentLevelId
import com.quizroom.utils.Constants.Companion.currentLevelNo
import com.quizroom.utils.Constants.Companion.questionsResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class AdapterLevel(
    var context: Context,
    levelsList: ArrayList<Level>,
    levelsImg: ArrayList<Int>,
    apiKey: String,
    progressDialog: Dialog
) :
    RecyclerView.Adapter<AdapterLevel.LevelViewHolder>() {

    var levelsList = ArrayList<Level>()
    var levelsImg = ArrayList<Int>()
    var counter = 0
    var appPref: AppPref
    var apiKey: String
    private lateinit var result: Call<Questions>
    private var progressDialog: Dialog


    init {
        this.apiKey = apiKey
        this.levelsList.addAll(levelsList)
        this.levelsImg.addAll(levelsImg)
        this.progressDialog = progressDialog
        appPref = AppPref(context)
    }

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivStar1: ImageView
        var ivStar2: ImageView
        var ivStar3: ImageView
        var clLevelParent: ConstraintLayout
        var clLevelBkg: ConstraintLayout
        var tvLevelNumber: TextView

        init {
            ivStar1 = itemView.findViewById(R.id.ivStar1)
            ivStar2 = itemView.findViewById(R.id.ivStar2)
            ivStar3 = itemView.findViewById(R.id.ivStar3)
            clLevelParent = itemView.findViewById(R.id.clLevelParent)
            clLevelBkg = itemView.findViewById(R.id.clLevelBkg)
            tvLevelNumber = itemView.findViewById(R.id.tvLevelNumber)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        return LevelViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.level_item, null)
        )
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val strLevelNum = String.format(Locale.ENGLISH, "%02d", position + 1)
        holder.tvLevelNumber.text = strLevelNum
        val levelId = levelsList[position].id
        when (getLevelStars(levelId)) {
            3 -> {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star)
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star)
                holder.ivStar3.setImageResource(R.drawable.ic_filled_star)
            }
            2 -> {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star)
                holder.ivStar2.setImageResource(R.drawable.ic_filled_star)
            }
            1 -> {
                holder.ivStar1.setImageResource(R.drawable.ic_filled_star)
            }
        }
        holder.clLevelBkg.setOnClickListener {
            progressDialog.show()
            getQuestions(position,it)
        }
        holder.clLevelBkg.background = AppCompatResources.getDrawable(
            context,
            levelsImg[counter]
        )
        counter++
        if (counter > 3) {
            counter = 0
        }
        if (position > 0){
            if (getLevelStars(levelsList[position-1].id) < 1){
                holder.clLevelParent.alpha = 0.5f
                holder.clLevelBkg.isClickable = false
            }
        }
    }

    override fun getItemCount(): Int {
        return levelsList.size
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getQuestions(position: Int, view: View) {
        val levelId = levelsList[position].id
        currentLevelId = levelId
        val quizApi = RetrofitInstance.api
        GlobalScope.launch {
            result = quizApi.getQuestions(levelId,apiKey)
            result.enqueue(object : Callback<Questions>{
                override fun onResponse(call: Call<Questions>, response: Response<Questions>) {
                    if (response.body() != null){
                        questionsResponse = response.body()!!
                        currentLevelNo = position+1
                        view.findNavController().navigate(R.id.action_levelsFragment_to_questionActivity)
                    }
                    progressDialog.dismiss()
                }
                override fun onFailure(call: Call<Questions>, t: Throwable) {
                    progressDialog.dismiss()
                    Log.d("CLEAR","AdapterLevel: ${t.message}")
                    Toast.makeText(context,"Unable to fetch data!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun getLevelStars(levelId: Int): Int {
        val playedLevels = appPref.getString(AppPref.PLAYED_LEVELS).toString()
        val pattern1 = Pattern.compile(",")
        val levels = pattern1.split(playedLevels)
        var starNo = 0
        for (level in levels){
            if (level.isNotEmpty()){
                val pattern2 = Pattern.compile(":")
                val star = pattern2.split(level)
                if (star[0].toInt() == levelId){
                    if (star[1].toInt() > starNo){
                        starNo  = star[1].toInt()
                    }
                }
            }
        }
        return starNo
    }
}