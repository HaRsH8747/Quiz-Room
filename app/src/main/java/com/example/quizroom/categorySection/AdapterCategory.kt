package com.example.quizroom.categorySection

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.quizroom.R
import com.example.quizroom.api.RetrofitInstance
import com.example.quizroom.models.Category
import com.example.quizroom.models.Levels
import com.example.quizroom.utils.Constants.Companion.currentCategory
import com.example.quizroom.utils.Constants.Companion.levelsResponse
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterCategory(
    list: ArrayList<Category>?,
    context: Context,
    apiKey: String,
    progressDialog: Dialog
) :

    RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
    var categoryArrayList = ArrayList<Category>()
    var context: Context
    var apiKey: String
    private lateinit var result: Call<Levels>
    private var progressDialog: Dialog

    init {
        this.apiKey = apiKey
        categoryArrayList.addAll(list!!)
        this.context = context
        this.progressDialog = progressDialog
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvContainer: CardView
        var clBackground: ConstraintLayout
        var ivCategory: ImageView

        init {
            cvContainer = itemView.findViewById(R.id.cvContainer)
            clBackground = itemView.findViewById(R.id.clBackground)
            ivCategory = itemView.findViewById(R.id.ivCategory)
        }
    }

    private fun convertDpToPixels(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, null)
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val marginLayoutParams = holder.cvContainer.layoutParams as MarginLayoutParams
        if (position % 2 != 0) {
            marginLayoutParams.setMargins(
                convertDpToPixels(context, 14),
                convertDpToPixels(context, 72),
                convertDpToPixels(context, 14),
                0
            )
        } else {
            marginLayoutParams.setMargins(
                convertDpToPixels(context, 14),
                0,
                convertDpToPixels(context, 14),
                convertDpToPixels(context, 50)
            )
        }
        holder.cvContainer.requestLayout()
        Picasso.get().load(categoryArrayList[position].icon).into(holder.ivCategory)
        holder.cvContainer.setOnClickListener {
            progressDialog.show()
            getLevels(position,it)
        }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getLevels(position: Int, view: View) {
        val catId = categoryArrayList[position].id
        currentCategory = catId
        val quizApi = RetrofitInstance.api
        GlobalScope.launch {
            result = quizApi.getLevels(catId,apiKey)
            result.enqueue(object : Callback<Levels>{
                override fun onResponse(call: Call<Levels>, response: Response<Levels>) {
                    if (response.body() != null){
                        levelsResponse = response.body()!!
                        val action = CategoryFragmentDirections.actionCategoryFragmentToLevelsFragment(apiKey)
                        view.findNavController().navigate(action)
                    }
                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<Levels>, t: Throwable) {
                    progressDialog.dismiss()
                    Log.d("CLEAR","AdapterCategory: ${t.message}")
                    Toast.makeText(context,"Unable to fetch data!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
