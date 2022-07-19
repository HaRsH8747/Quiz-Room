package com.example.quizroom

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.example.quizroom.databinding.ActivityQuizBinding
import com.example.quizroom.utils.AppPref
import com.example.quizroom.utils.ConnectionLiveData

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var connectionLiveData: ConnectionLiveData
    var appPref: AppPref? = null
    private var totalCoins = 0
    private var userRank = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addNetworkListener(this)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onResume() {
        super.onResume()
        appPref = AppPref(this)
        totalCoins = appPref!!.getInt(AppPref.TOTAL_COINS)
        userRank = appPref!!.getInt(AppPref.USER_RANK)

        if (userRank == 0){
            userRank = 1500
            appPref!!.setInt(AppPref.USER_RANK,userRank)
        }else{
            userRank -= totalCoins/20
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        binding.tvTotalCoins.text = totalCoins.toString()
        binding.tvUserRank.text = userRank.toString()
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
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
        connectionLiveData.observe(this, Observer {isAvailable->
            when(isAvailable)
            {
                true-> {
                    if (networkDialog.isShowing){
                        networkDialog.dismiss()
                    }
                }
                false-> {
                    networkDialog.show()
                }
            }
        })
    }
}