package com.example.revisly.Screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.revisly.Adapters.FullscreenImageAdapter
import com.example.revisly.R
import com.example.revisly.databinding.FragmentFullViewBinding

class FullViewFragment : Fragment() {
    lateinit var binding: FragmentFullViewBinding
    private var imagelist : MutableList<String>? = null

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
        binding = FragmentFullViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagelist = arguments?.getStringArrayList("Images" ?: "")
        val postion : Int? = arguments?.getInt("Postion", 0)

        binding.ImageSet.adapter = FullscreenImageAdapter(imagelist)
        binding.ImageSet.setCurrentItem(postion!!, false)
        binding.indicator.setViewPager(binding.ImageSet)


    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FullViewFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}