package com.example.revisly.Screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.revisly.Adapters.OpenImageAdapter
import com.example.revisly.FlattenedImage
import com.example.revisly.MainActivity
import com.example.revisly.R
import com.example.revisly.SavesData
import com.example.revisly.databinding.FragmentOpenImageBinding

class OpenImageFragment : Fragment() {

    lateinit var binding: FragmentOpenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOpenImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity)?.binding?.bottomBar?.visibility = View.GONE


        val flattenedImages = arguments?.getParcelableArrayList<SavesData>("Savelist") ?: return
        val startIndex = arguments?.getInt("startIndex") ?: 0
//        val groupedItems: List<List<FlattenedImage>> = flattenedImages
//            .groupBy { it.itemIndex }
//            .toSortedMap() // ensures item order is preserved
//            .values
//            .toList()

        val adapter = OpenImageAdapter(flattenedImages)

        binding.ShowsImageHandler.adapter = adapter
        binding.ShowsImageHandler.setCurrentItem(startIndex, false)



    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OpenImageFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity)?.binding?.bottomBar?.visibility = View.VISIBLE
    }
}