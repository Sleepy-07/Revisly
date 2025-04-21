package com.example.revisly.Adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.revisly.R
import com.github.chrisbanes.photoview.PhotoView

class FullImagePagerAdapter(
    private val images: List<String>,
    private val titles: String,
    private val sources: String,
    private val sourceUrls: String
) : RecyclerView.Adapter<FullImagePagerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: PhotoView = view.findViewById(R.id.fullImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_full_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = images[position]
        
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val originalWidth = resource.width
                    val originalHeight = resource.height
                    
                    // Calculate aspect ratio
                    val aspectRatio = originalHeight.toFloat() / originalWidth
                    
                    // Set image dimensions based on screen width
                    val screenWidth = holder.itemView.resources.displayMetrics.widthPixels
                    val targetHeight = (screenWidth * aspectRatio).toInt()
                    
                    // Update layout params
                    holder.imageView.layoutParams.height = targetHeight
                    holder.imageView.requestLayout()
                    
                    // Set image
                    holder.imageView.setImageBitmap(resource)
                    
                    // Enable zooming
                    holder.imageView.setZoomable(true)
                    holder.imageView.setMaximumScale(5f)
                    holder.imageView.setMediumScale(2f)
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    holder.imageView.setImageDrawable(placeholder)
                }
            })
    }

    override fun getItemCount(): Int = images.size
} 