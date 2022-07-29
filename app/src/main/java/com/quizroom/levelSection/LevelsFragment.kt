package com.quizroom.levelSection

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.quizroom.R
import com.quizroom.databinding.FragmentLevelsBinding
import com.quizroom.models.Level
import com.quizroom.utils.Utils.Companion.levelsResponse

class LevelsFragment : Fragment() {

    private lateinit var binding: FragmentLevelsBinding
    var levelsList = ArrayList<Level>()
    var levelsImg = ArrayList<Int>()
    val args: LevelsFragmentArgs by navArgs()
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLevelsBinding.inflate(inflater,container,false)
        progressDialog = Dialog(requireContext())
        progressDialog.setContentView(R.layout.progress_circular)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        levelsImg.add(R.drawable.ic_level_t1)
        levelsImg.add(R.drawable.ic_level_t2)
        levelsImg.add(R.drawable.ic_level_t3)
        levelsImg.add(R.drawable.ic_level_t4)

        for (item in levelsResponse.category){
            levelsList.add(Level(item.id,item.title))
        }

        val adapterLevel = AdapterLevel(requireContext(), levelsList, levelsImg, args.apiKey, progressDialog)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLevels.layoutManager = gridLayoutManager
        binding.rvLevels.adapter = adapterLevel


        return binding.root
    }
}