package com.quizroom.resultSection

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.quizroom.HomeActivity
import com.quizroom.questionSection.QuestionActivity
import com.quizroom.QuizActivity
import com.quizroom.R
import com.quizroom.databinding.ActivityFailBinding
import com.quizroom.utils.AppPref
import com.quizroom.utils.ConnectionLiveData
import com.quizroom.utils.Utils.Companion.currentRightAnswers

class FailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFailBinding
    private var appPref: AppPref? = null
    private lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFailBinding.inflate(layoutInflater)
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
        appPref = AppPref(this)
        binding.btnPlayAgain.setOnClickListener{
            val intent = Intent(this, QuestionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        currentRightAnswers = 0
        binding.btnNewQuiz2.setOnClickListener{ onBackPressed() }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        binding.btnBack5.setOnClickListener{ onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, QuizActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
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