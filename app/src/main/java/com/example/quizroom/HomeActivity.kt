package com.example.quizroom

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
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.example.quizroom.QuizApp.Companion.musicPlayer
import com.example.quizroom.databinding.ActivityHomeBinding
import com.example.quizroom.utils.ConnectionLiveData
import com.example.quizroom.utils.Constants.Companion.isAudioEnabled


class HomeActivity : AppCompatActivity(){

    private lateinit var binding: ActivityHomeBinding
    private lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
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
        if (isAudioEnabled){
            binding.ivSound.setImageResource(R.drawable.sound_on)
        }else{
            binding.ivSound.setImageResource(R.drawable.sound_off)
        }

        binding.ivSound.setOnClickListener {
            isAudioEnabled = !isAudioEnabled
            if (isAudioEnabled){
                binding.ivSound.setImageResource(R.drawable.sound_on)
                musicPlayer!!.start()
            }else{
                musicPlayer!!.pause()
                binding.ivSound.setImageResource(R.drawable.sound_off)
            }
        }
        binding.btnPlay.setOnClickListener{
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
//    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
//        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
//            if (serviceClass.name == service.service.className) {
//                return true
//            }
//        }
//        return false
//    }

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