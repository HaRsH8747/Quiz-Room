package com.quizroom.resultSection

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.quizroom.BuildConfig
import com.quizroom.HomeActivity
import com.quizroom.QuizActivity
import com.quizroom.R
import com.quizroom.databinding.ActivityWinBinding
import com.quizroom.utils.AppPref
import com.quizroom.utils.ConnectionLiveData
import com.quizroom.utils.Utils.Companion.currentRightAnswers
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*


class WinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWinBinding
    private var appPref: AppPref? = null
    private var file: String = ""
    private lateinit var progressDialog: Dialog
    private lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWinBinding.inflate(layoutInflater)
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
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progress_circular)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        appPref = AppPref(this)
//        val hasWon = appPref!!.getBoolean(AppPref.HAS_WON)
        val rightAns = currentRightAnswers
        binding.tvWinCoins.text = "+${rightAns * 10}"
//        if (hasWon){
//            binding.tvWinCoins.text = "+${rightAns * 10}"
//        }else{
//            binding.tvWinCoins.visibility = View.GONE
//            binding.textView3.visibility = View.GONE
//            binding.textView4.visibility = View.GONE
//        }
        currentRightAnswers = 0
//        appPref!!.setBoolean(AppPref.HAS_WON,false)
        binding.btnShare.setOnClickListener {
            progressDialog.show()
            lifecycleScope.launch {
                takeScreenShot(binding.clWin)
            }
        }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        binding.btnBack4.setOnClickListener{ onBackPressed() }
        binding.btnNewQuiz.setOnClickListener{ onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        rateApplication()
    }

    private fun rateApplication(){
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("CLEAR","request Successful")
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {
                    Log.d("CLEAR","launch Successful")
                }
            } else {
                goToPlayStore()
            }
        }
    }

    private fun goToPlayStore() {
        val uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, QuizActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        if (file.isNotEmpty()){
            deletePhotoFromInternalStorage(file)
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

//    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
//            inImage,
//            "Title",
//            null)
//        return Uri.parse(path)
//    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private suspend fun takeScreenShot(view: View){
        withContext(Dispatchers.IO){
            val date = Date()
            val format: String = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date).toString()
            val bmp = getBitmapFromView(view)
            if (bmp != null) {
                savePhotoToInternalStorage(format,bmp)
            }else{
                progressDialog.dismiss()
            }
        }
    }

    private suspend fun savePhotoToInternalStorage(filename: String, bmp: Bitmap){
        withContext(Dispatchers.IO){
            try {
                openFileOutput("$filename.png", MODE_PRIVATE).use { stream ->
                    if (!bmp.compress(Bitmap.CompressFormat.PNG,100,stream)){
                        progressDialog.dismiss()
                        throw IOException("Couldn't save bitmap")
                    }
                    loadPhotoFromInternalStorage(filename)
                }
            }catch (e: Exception){
                progressDialog.dismiss()
                e.printStackTrace()
                Log.d("CLEAR","msg: ${e.message}")
            }
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            deletePhotoFromInternalStorage(file)
        }
    }

    private suspend fun loadPhotoFromInternalStorage(fileName: String){
        try {
            withContext(Dispatchers.IO){
                val files = filesDir.listFiles()
                files?.filter { it.canRead() && it.isFile && it.name.endsWith(".png") && it.name.equals("$fileName.png") }?.map {
                    val photoUri = FileProvider.getUriForFile(this@WinActivity, "${BuildConfig.APPLICATION_ID}.provider",it)
                    val intent = Intent(Intent.ACTION_SEND)
                    val coins = appPref!!.getInt(AppPref.TOTAL_COINS)
                    val rank = appPref!!.getInt(AppPref.USER_RANK) - coins/20
                    intent.type = "image/png"
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    intent.putExtra(Intent.EXTRA_STREAM, photoUri)
                    intent.putExtra(Intent.EXTRA_TEXT,"I have earned $coins coins and my rank is $rank \n\n You can also download this amazing Quiz Application:\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    progressDialog.dismiss()
                    resultLauncher.launch(Intent.createChooser(intent,"Share Victory!!"))
                    file = "$fileName.png"
                }
            }
        }catch (e: Exception){
            progressDialog.dismiss()
            e.printStackTrace()
            Log.d("CLEAR","msg: ${e.message}")
        }
    }

    private fun deletePhotoFromInternalStorage(filename: String): Boolean {
        return try {
            val dir: File = filesDir
            val file = File(dir, filename)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
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
                }
                false -> {
                    networkDialog.show()
                }
            }
        }
    }
}