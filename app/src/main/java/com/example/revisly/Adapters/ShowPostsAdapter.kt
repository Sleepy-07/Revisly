package com.example.revisly.Adapters

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.example.revisly.Posts
import com.example.revisly.R
import com.bumptech.glide.request.target.CustomTarget
import com.example.revisly.SavesData
import me.relex.circleindicator.CircleIndicator3
import kotlin.math.min

class ShowPostsAdapter(private val list: List<SavesData>, val click: FullView) : RecyclerView.Adapter<ShowPostsAdapter.ViewHolder>() {

    lateinit var viewPagerAdapter: ViewPagerAdapter
    var mainposition: Int = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewPager: ViewPager2 = view.findViewById(R.id.ImageSet)
        val indicator: CircleIndicator3 = view.findViewById(R.id.indicator)
    }

    private val animatedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_showpposts, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val images = reconstructUrls(item.images)
        mainposition = position

        // Entrance animation (only once per item)
        if (!animatedPositions.contains(position)) {
            holder.itemView.alpha = 0f
            holder.itemView.translationY = 100f
            holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start()
            animatedPositions.add(position)
        } else {
            holder.itemView.alpha = 1f
            holder.itemView.translationY = 0f
        }

        // Set up ViewPager2 adapter
        val adapter = ImagePagerAdapter(images) { imagePosition ->
            // Navigate to full screen view
            val bundle = Bundle().apply {
                putStringArrayList("images", ArrayList(images))
                putString("titles", item.title)
                putString("accountname" , item.account_name)
                putString("sources", item.platform)
                putString("sourceUrls", item.url)
                putInt("position", position)
            }
            holder.itemView.findNavController().navigate(R.id.action_postViewFragment_to_fullImageViewFragment2, bundle)
        }
        holder.viewPager.adapter = adapter
        holder.indicator.setViewPager(holder.viewPager)

        if (images.size == 1) {
            holder.indicator.visibility = View.GONE
        }

        // Detect orientation
        val orientation = holder.itemView.resources.configuration.orientation

        // Set ViewPager2 height based on first image
        if (images.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(images[0])
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                        val originalWidth = resource.width
                        val originalHeight = resource.height

                        val imageViewWidth = holder.viewPager.width.takeIf { it > 0 }
                            ?: (holder.viewPager.resources.displayMetrics.widthPixels / 3)

                        val aspectRatio = originalHeight.toFloat() / originalWidth
                        var targetHeight = (imageViewWidth * aspectRatio).toInt()

                        // Apply max height based on orientation
                        val maxHeight = if (orientation == Configuration.ORIENTATION_PORTRAIT) 1000 else 800
                        targetHeight = min(targetHeight, maxHeight)

                        holder.viewPager.layoutParams.height = targetHeight
                        holder.viewPager.requestLayout()
                    }

                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
                })
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = position

    // Inner adapter for ViewPager2
    private inner class ImagePagerAdapter(private val images: List<String>, private val onImageClick: (Int) -> Unit) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

        inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.imageView)
            init {
                imageView.setOnClickListener {
                    onImageClick(adapterPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val imageUrl = images[position]
            holder.imageView.scaleType = ImageView.ScaleType.MATRIX

            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                        val originalWidth = resource.width
                        val originalHeight = resource.height

                        val imageViewWidth = holder.imageView.width.takeIf { it > 0 }
                            ?: (holder.imageView.resources.displayMetrics.widthPixels / 3)

                        val scale = imageViewWidth.toFloat() / originalWidth

                        val matrix = Matrix()
                        matrix.setScale(scale, scale)
                        holder.imageView.imageMatrix = matrix

                        holder.imageView.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
                })
        }

        override fun getItemCount(): Int = images.size
    }

    interface FullView {
        fun openFullView(postPosition: Int, imagePosition: Int)
    }

    fun reconstructUrls(rawItems: List<Any?>?): List<String> {
        if (rawItems == null) return emptyList()

        val rawString = rawItems.joinToString(",") { it.toString().trim() }
        val parts = rawString.split(",")
        val urls = mutableListOf<String>()
        var currentUrl = ""

        for (part in parts) {
            val trimmed = part.trim()
            if (trimmed.startsWith("https://")) {
                if (currentUrl.isNotEmpty()) {
                    urls.add(currentUrl)
                }
                currentUrl = trimmed
            } else {
                currentUrl += ",$trimmed"
            }
        }

        if (currentUrl.isNotEmpty()) {
            urls.add(currentUrl)
        }

        return urls
    }
}
