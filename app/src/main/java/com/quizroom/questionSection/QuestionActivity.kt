package com.quizroom.questionSection

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.quizroom.QuizApp
import com.quizroom.R
import com.quizroom.resultSection.FailActivity
import com.quizroom.resultSection.WinActivity
import com.quizroom.utils.AppPref
import com.quizroom.databinding.ActivityQuestionBinding
import com.quizroom.models.Question
import com.quizroom.utils.ConnectionLiveData
import com.quizroom.utils.Constants.Companion.currentLevelId
import com.quizroom.utils.Constants.Companion.currentLevelNo
import com.quizroom.utils.Constants.Companion.currentRightAnswers
import com.quizroom.utils.Constants.Companion.questionsResponse

class QuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBinding
    var questionArrayList = ArrayList<Question>()
    var radioGroupArrayList = ArrayList<RadioGroup>()
    var strLevelNumber: String? = null
    private var appPref: AppPref? = null
    var isAnswered = false
    private lateinit var playedLevels: String
    private lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        addNetworkListener(this)
        QuizApp.musicPlayer!!.setVolume(0.3f,0.3f)
        appPref = AppPref(this)
        binding.vpQuestions.offscreenPageLimit = 3
        binding.btnBack3.setOnClickListener{
            if (binding.vpQuestions.currentItem - 1 < 0) {
                onBackPressed()
            } else {
                binding.vpQuestions.currentItem = binding.vpQuestions.currentItem - 1
                isOptionSelected(binding.vpQuestions.currentItem)
            }
        }
        binding.btnForward.setOnClickListener{
            if (binding.vpQuestions.currentItem == questionArrayList.size - 1) {
                checkResult()
            } else {
                binding.vpQuestions.currentItem = binding.vpQuestions.currentItem + 1
                isOptionSelected(binding.vpQuestions.currentItem)
            }
        }
        strLevelNumber = appPref!!.getString(AppPref.LEVEL_NUMBER)

        for (question in questionsResponse.category.shuffled()){
            questionArrayList.add(Question(question.id,question.answers,question.que_image,question.question))
        }

        val viewPagerAdapter = ViewPagerAdapter(
            this, questionArrayList, binding.lvWinAnim, appPref!!, currentLevelNo
        )
        binding.vpQuestions.isUserInputEnabled = false
        binding.vpQuestions.adapter = viewPagerAdapter
    }

    override fun onStart() {
        super.onStart()
        changeVisibility(false)
    }

    override fun onResume() {
        super.onResume()
        if (radioGroupArrayList.size > 0){
            isOptionSelected(binding.vpQuestions.currentItem)
        }
    }

    fun setRadioGroupItems(radioGroup: RadioGroup) {
        radioGroupArrayList.add(radioGroup)
    }

    private fun isOptionSelected(viewPageIndex: Int) {
        changeVisibility(radioGroupArrayList[viewPageIndex].checkedRadioButtonId != -1)
    }

    fun setIsAnswered(isAnswered: Boolean) {
        this.isAnswered = isAnswered
        changeVisibility(isAnswered)
    }

    private fun changeVisibility(isAnswered: Boolean) {
        if (isAnswered) {
            binding.btnForward.alpha = 1f
            binding.btnForward.isClickable = true
        } else {
            binding.btnForward.alpha = 0.5f
            binding.btnForward.isClickable = false
        }
    }

    private fun checkResult() {
        val rightAnswers = currentRightAnswers
        val percentage = ((rightAnswers.toDouble()/questionArrayList.size) * 100).toInt()
        QuizApp.musicPlayer!!.setVolume(1.0f,1.0f)
        if (percentage in 25..100) {
            setPlayedLevels()
            addCoins(rightAnswers)
            val intent = Intent(this, WinActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, FailActivity::class.java)
            startActivity(intent)
            finish()
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

//    private fun getEligibilityForCoins(levelId: Int, currentStar: Int): Boolean {
//        var isEligible = false
//        val playedLevels = appPref?.getString(AppPref.PLAYED_LEVELS).toString()
//        val pattern1 = Pattern.compile(",")
//        val levels = pattern1.split(playedLevels)
//        var starNo = 0
//        for (level in levels){
//            if (level.isNotEmpty()){
//                val pattern2 = Pattern.compile(":")
//                val star = pattern2.split(level)
//                if (star[0].toInt() == levelId){
//                    if (star[1].toInt() > starNo){
//                        starNo  = star[1].toInt()
//                    }
//                }
//            }
//        }
//        if (currentStar > starNo){
//            isEligible = true
//        }
//        return isEligible
//    }

    private fun setPlayedLevels() {
        val rightAnswers = currentRightAnswers
        val totalQuestion = questionArrayList.size
//        var currentStar = 0
        playedLevels = appPref?.getString(AppPref.PLAYED_LEVELS).toString()
        if (rightAnswers > 0){
            when (((rightAnswers.toDouble()/totalQuestion) * 100).toInt()) {
                in 25..50 -> {
                    playedLevels = "$playedLevels$currentLevelId:1,"
//                    currentStar = 1
                }
                in 51..75 -> {
                    playedLevels = "$playedLevels$currentLevelId:2,"
//                    currentStar = 2
                }
                in 76..100 -> {
                    playedLevels = "$playedLevels$currentLevelId:3,"
//                    currentStar = 3
                }
            }
//            if (getEligibilityForCoins(currentLevelId,currentStar)) {
//                appPref!!.setBoolean(AppPref.HAS_WON,true)
//                addCoins(rightAnswers)
//            }
            appPref?.setString(AppPref.PLAYED_LEVELS,playedLevels)
        }
    }

    private fun addCoins(rightAnswers: Int) {
        appPref!!.setInt(
            AppPref.TOTAL_COINS,
            appPref!!.getInt(AppPref.TOTAL_COINS) + rightAnswers * 10
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        QuizApp.musicPlayer!!.setVolume(1.0f,1.0f)
        currentRightAnswers = 0
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun addNetworkListener(context: Context){
        val networkDialog = Dialog(context)
        networkDialog.setContentView(R.layout.check_network_dialog)
        val lottieAnimation = networkDialog.findViewById<LottieAnimationView>(R.id.lvInternet)
        networkDialog.setCancelable(false)
        networkDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        networkDialog.show()
        lottieAnimation.playAnimation()
        connectionLiveData= ConnectionLiveData(application)
        connectionLiveData.observe(this) { isAvailable ->
            when (isAvailable) {
                true -> {
                    if (networkDialog.isShowing) {
                        networkDialog.dismiss()
                    }
                }
                false -> {
                    networkDialog.show()
                }
            }
        }
    }
}