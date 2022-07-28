package com.quizroom.categorySection

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.quizroom.R
import com.quizroom.databinding.FragmentCategoryBinding
import com.quizroom.models.Categories
import com.quizroom.models.Category
import com.quizroom.utils.Constants.Companion.categoryResponse
import retrofit2.Response

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    var categoriesList = ArrayList<Category>()
    private lateinit var result: Response<Categories>
    private lateinit var progressDialog: Dialog
    companion object {
        init {
            System.loadLibrary("quizroom")
        }
    }
    private external fun getAPIKey(): String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(inflater,container,false)

        progressDialog = Dialog(requireContext())
        progressDialog.setContentView(R.layout.progress_circular)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        for (item in categoryResponse.category){
            categoriesList.add(Category(item.icon, item.id,item.title))
        }

        val adapterCategory = AdapterCategory(categoriesList, requireContext(),getAPIKey(),progressDialog)

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategories.layoutManager = gridLayoutManager
        binding.rvCategories.adapter = adapterCategory

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoriesList.clear()
    }
}