package com.example.revisly.Screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.revisly.Adapters.FullImagePagerAdapter
import com.example.revisly.MainActivity
import com.example.revisly.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import me.relex.circleindicator.CircleIndicator3

class FullImageViewFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var closeButton: MaterialButton
    private lateinit var imageTitle: MaterialTextView
    private lateinit var imageaccount: MaterialTextView
    private lateinit var imageSource: MaterialTextView
    private lateinit var visitSourceButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_full_image_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity)?.binding?.bottomBar?.visibility = View.GONE

        viewPager = view.findViewById(R.id.fullImageViewPager)
        indicator = view.findViewById(R.id.fullImageIndicator)
        closeButton = view.findViewById(R.id.closeButton)
        imageTitle = view.findViewById(R.id.imageTitle)
        imageaccount = view.findViewById(R.id.imageaccount)
        imageSource = view.findViewById(R.id.imageSource)
        visitSourceButton = view.findViewById(R.id.visitSourceButton)

        val images = arguments?.getStringArrayList("images") ?: return
        val titles = arguments?.getString("titles") ?: return
        val accountname = arguments?.getString("accountname") ?: return
        val sources = arguments?.getString("sources") ?: return
        val sourceUrls = arguments?.getString("sourceUrls") ?: return
        val startPosition = arguments?.getInt("position", 0) ?: 0

        Log.e("Data pass is ", "onViewCreated: title = $titles \nsource = $sources", )




        val adapter = FullImagePagerAdapter(images, titles, sources, sourceUrls)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(startPosition, false)
        indicator.setViewPager(viewPager)

        if(images.size > 1) {
            indicator.visibility = View.VISIBLE
        } else {
            indicator.visibility = View.GONE
        }

        // Update title and source for initial position
        updateImageInfo(startPosition)

        // Handle page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateImageInfo(position)
            }
        })

        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        visitSourceButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrls))
            startActivity(intent)
        }
    }

    private fun updateImageInfo(position: Int) {
        val titles = arguments?.getString("titles") ?: return
        val sources = arguments?.getString("sources") ?: return
        val accountname = arguments?.getString("accountname") ?: return


        if(accountname == "ideas"){
            imageaccount.visibility = View.GONE
        }else{
            imageaccount.visibility = View.VISIBLE
            imageaccount.text = accountname
        }
        
        imageTitle.text = titles
        imageSource.text = sources
        visitSourceButton.text = "Visit $sources"

    }

    override fun onDestroy() {
        (activity as MainActivity)?.binding?.bottomBar?.visibility = View.VISIBLE

        super.onDestroy()

    }
} 