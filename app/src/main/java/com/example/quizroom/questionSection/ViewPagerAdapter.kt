package com.example.quizroom.questionSection

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.quizroom.R
import com.example.quizroom.models.Question
import com.example.quizroom.utils.AppPref
import com.example.quizroom.utils.Constants.Companion.currentRightAnswers
import com.example.quizroom.utils.Constants.Companion.isAudioEnabled
import com.google.android.material.radiobutton.MaterialRadioButton
import com.squareup.picasso.Picasso

class ViewPagerAdapter(
    var questionActivity: QuestionActivity,
    questionArrayList: ArrayList<Question>,
    lvWinAnim: LottieAnimationView,
    appPref: AppPref,
    levelId: Int
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    private var questionArrayList = ArrayList<Question>()
    private var lvWinAnim: LottieAnimationView
    private var appPref: AppPref
    private var levelNo: Int
    private var correctAnsMusic: MediaPlayer? = null
    private var wrongAnsMusic: MediaPlayer? = null

    init {
        this.questionArrayList.addAll(questionArrayList)
        this.lvWinAnim = lvWinAnim
        this.appPref = appPref
        this.levelNo = levelId
        if (isAudioEnabled){
            correctAnsMusic = MediaPlayer.create(questionActivity, R.raw.correct_ans)
            correctAnsMusic!!.isLooping = false
            wrongAnsMusic = MediaPlayer.create(questionActivity, R.raw.wrong_ans)
            wrongAnsMusic!!.isLooping = false
        }
    }

    inner class ViewPagerViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvQLevelNumber: TextView
        var tvQuestionNumber: TextView
        var tvQuestion: TextView
        var ivQuestion: ImageView
        var rgOptions: RadioGroup
        var rbOption1: MaterialRadioButton
        var rbOption2: MaterialRadioButton
        var rbOption3: MaterialRadioButton
        var rbOption4: MaterialRadioButton

        init {
            tvQLevelNumber = itemView.findViewById(R.id.tvQLevelNumber)
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber)
            tvQuestion = itemView.findViewById(R.id.tvQuestion)
            ivQuestion = itemView.findViewById(R.id.ivQuestion)
            rgOptions = itemView.findViewById(R.id.rgOptions)
            rbOption1 = itemView.findViewById(R.id.rbOption1)
            rbOption2 = itemView.findViewById(R.id.rbOption2)
            rbOption3 = itemView.findViewById(R.id.rbOption3)
            rbOption4 = itemView.findViewById(R.id.rbOption4)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        return ViewPagerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.question_item, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.tvQLevelNumber.text = "Level $levelNo"
        holder.tvQuestionNumber.text = "${position+1}/${questionArrayList.size}"
        holder.tvQuestion.text = questionArrayList[position].question
        if (questionArrayList[position].que_image == "false"){
            holder.ivQuestion.visibility = View.GONE
        }else{
            Picasso.get().load(questionArrayList[position].que_image).into(holder.ivQuestion)
        }
        var i = 0
        var answerIndex = 0
        val rbArrayList = ArrayList<MaterialRadioButton>()
        if (i < questionArrayList[position].answers.size && questionArrayList[position].answers[i].answer.isNotEmpty()){
            holder.rbOption1.text = questionArrayList[position].answers[i].answer
            rbArrayList.add(holder.rbOption1)
            if (questionArrayList[position].answers[i].true_answer){
                answerIndex = i
            }
        }else {
            holder.rbOption1.visibility = View.INVISIBLE
        }
        i++

        if (i < questionArrayList[position].answers.size && questionArrayList[position].answers[i].answer.isNotEmpty()){
            holder.rbOption2.text = questionArrayList[position].answers[i].answer
            rbArrayList.add(holder.rbOption2)
            if (questionArrayList[position].answers[i].true_answer){
                answerIndex = i
            }
        }else {
            holder.rbOption2.visibility = View.INVISIBLE
        }
        i++

        if (i < questionArrayList[position].answers.size && questionArrayList[position].answers[i].answer.isNotEmpty()){
            holder.rbOption3.text = questionArrayList[position].answers[i].answer
            rbArrayList.add(holder.rbOption3)
            if (questionArrayList[position].answers[i].true_answer){
                answerIndex = i
            }
        }else {
            holder.rbOption3.visibility = View.INVISIBLE
        }
        i++

        if (i < questionArrayList[position].answers.size && questionArrayList[position].answers[i].answer.isNotEmpty()){
            holder.rbOption4.text = questionArrayList[position].answers[i].answer
            rbArrayList.add(holder.rbOption4)
            if (questionArrayList[position].answers[i].true_answer){
                answerIndex = i
            }
        }else {
            holder.rbOption4.visibility = View.INVISIBLE
        }
        questionActivity.setRadioGroupItems(holder.rgOptions)
        holder.rgOptions.setOnCheckedChangeListener { radioGroup: RadioGroup?, i: Int ->
            questionActivity.setIsAnswered(true)
            val selectedId = holder.rgOptions.checkedRadioButtonId
            val materialRadioButton = holder.rgOptions.findViewById<MaterialRadioButton>(selectedId)
            val selectedIndex = holder.rgOptions.indexOfChild(materialRadioButton)
            checkAnswer(selectedIndex, answerIndex, rbArrayList, holder.rgOptions)
        }
    }

    override fun getItemCount(): Int {
        return questionArrayList.size
    }

    private fun checkAnswer(
        selectedIndex: Int,
        answer: Int,
        rbArrayList: ArrayList<MaterialRadioButton>,
        rgOptions: RadioGroup
    ) {
        if (selectedIndex != answer) {
            if (isAudioEnabled){
                wrongAnsMusic!!.start()
            }
            rbArrayList[selectedIndex].background = AppCompatResources.getDrawable(
                questionActivity, R.drawable.radio_wrong
            )
            rbArrayList[answer].background = AppCompatResources.getDrawable(
                questionActivity, R.drawable.radio_correct
            )
        } else {
            if (isAudioEnabled){
                correctAnsMusic!!.start()
            }
            currentRightAnswers += 1
            rbArrayList[selectedIndex].background = AppCompatResources.getDrawable(
                questionActivity, R.drawable.radio_correct
            )
            lvWinAnim.playAnimation()
        }
        rgOptions.setOnCheckedChangeListener { radioGroup: RadioGroup?, i: Int -> }
    }
}
