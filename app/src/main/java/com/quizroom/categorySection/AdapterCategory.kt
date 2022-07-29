package com.quizroom.categorySection

import android.app.Dialog
import android.content.Context
import android.net.Uri
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
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.quizroom.R
import com.quizroom.api.RetrofitInstance
import com.quizroom.models.Category
import com.quizroom.models.Levels
import com.quizroom.utils.Utils
import com.quizroom.utils.Utils.Companion.currentCategory
import com.quizroom.utils.Utils.Companion.levelsResponse
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
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
    private var httpClient: OkHttpClient? = null

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

        if(categoryArrayList[position].icon.contains(".svg")){
            GlideToVectorYou
                .init()
                .with(context)
                .setPlaceHolder(R.drawable.sports_ctg,R.drawable.sports_ctg)
                .load(Uri.parse(categoryArrayList[position].icon),holder.ivCategory)
        }else{
            Picasso.get().load(categoryArrayList[position].icon).into(holder.ivCategory)
        }
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
