package com.quizroom

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.quizroom.api.RetrofitInstance
import com.quizroom.models.Categories
import com.quizroom.utils.ConnectionLiveData
import com.quizroom.utils.Utils.Companion.categoryResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(){

    private lateinit var result: Call<Categories>
    companion object {
        init {
            System.loadLibrary("quizroom")
        }
    }
    private external fun getAPIKey(): String
    private lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchCategoryData(){
        val intent = Intent(this, HomeActivity::class.java)
        val quizApi = RetrofitInstance.api
        GlobalScope.launch {
            result = quizApi.getCategories(getAPIKey())
            result.enqueue(object : Callback<Categories>{
                override fun onResponse(call: Call<Categories>, response: Response<Categories>) {
                    if(response.body() != null){
                        categoryResponse = response.body()!!
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
                        finish()
                    }
                }
                override fun onFailure(call: Call<Categories>, t: Throwable) {
                    Log.d("CLEAR","MainActivity: ${t.message}")
                    Toast.makeText(this@MainActivity,"Unable to fetch data!",Toast.LENGTH_SHORT).show()
                }
            })
        }
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
                    fetchCategoryData()
                }
                false -> {
                    networkDialog.show()
                }
            }
        }
    }

}