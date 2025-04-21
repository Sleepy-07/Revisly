package com.example.revisly.Screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.revisly.Adapters.FlattenedImagePagerAdapter
import com.example.revisly.Extras.DynamicHeightViewPager2
import com.example.revisly.FlattenedImage
import com.example.revisly.MainActivity
import com.example.revisly.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import me.relex.circleindicator.CircleIndicator3

class FullImageViewFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var closeButton: MaterialButton
    private lateinit var imageTitle: MaterialTextView
    private lateinit var imageaccount: MaterialTextView
    private lateinit var imageSource: MaterialTextView
    private lateinit var visitSourceButton: FloatingActionButton

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

//        viewPager = view.findViewById(R.id.fullImageViewPager)
        indicator = view.findViewById(R.id.fullImageIndicator)
        closeButton = view.findViewById(R.id.closeButton)
        imageTitle = view.findViewById(R.id.imageTitle)
        imageaccount = view.findViewById(R.id.imageaccount)
        imageSource = view.findViewById(R.id.imageSource)
        visitSourceButton = view.findViewById(R.id.visitSourceButton)

        // 🧹 Old args no longer needed - using FlattenedImage list instead
        /*
        val images = arguments?.getStringArrayList("images") ?: return
        val titles = arguments?.getString("titles") ?: return
        val accountname = arguments?.getString("accountname") ?: return
        val sources = arguments?.getString("sources") ?: return
        val sourceUrls = arguments?.getString("sourceUrls") ?: return
        val startPosition = arguments?.getInt("position", 0) ?: 0
        */

        val flattenedImages = arguments?.getParcelableArrayList<FlattenedImage>("flattenedImages") ?: return
        val startIndex = arguments?.getInt("startIndex") ?: 0

        // ViewPager transition animation
        viewPager.setPageTransformer { page: View, position: Float ->
            page.alpha = 0f
            page.translationX = position * page.width
            page.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        val adapter = FlattenedImagePagerAdapter(flattenedImages)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(startIndex, false)
        indicator.setViewPager(viewPager)

        // Toggle indicator and swipe control
        if (flattenedImages.size > 1) {
            indicator.visibility = View.VISIBLE
            viewPager.isUserInputEnabled = true
        } else {
            indicator.visibility = View.GONE
            viewPager.isUserInputEnabled = false
        }

        // Update metadata for current image
        updateImageInfo(flattenedImages[startIndex])

        // Listen for page changes
        val container = view.findViewById<DynamicHeightViewPager2>(R.id.viewPagerContainer)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewPager.post {
                    val viewHolder = (viewPager.getChildAt(0) as RecyclerView)
                        .findViewHolderForAdapterPosition(position)?.itemView
                    viewHolder?.let { container.measureCurrentView(it) }
                }
            }
        })

        // Close button
        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Source button opens external link
        visitSourceButton.setOnClickListener {
            val url = flattenedImages[viewPager.currentItem].parentItem.url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun updateImageInfo(flattenedImage: FlattenedImage) {
        val parent = flattenedImage.parentItem

        if (parent.account_name == "ideas") {
            imageaccount.visibility = View.GONE
        } else {
            imageaccount.visibility = View.VISIBLE
            imageaccount.text = parent.account_name
        }

        imageTitle.text = parent.title
        imageSource.text = parent.platform
    }

    override fun onDestroy() {
        (activity as MainActivity)?.binding?.bottomBar?.visibility = View.VISIBLE
        arguments?.putInt("position", 0) // Not really used anymore, but left for compatibility
        super.onDestroy()
    }
}
