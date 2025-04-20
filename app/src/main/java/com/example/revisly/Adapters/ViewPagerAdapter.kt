package com.example.revisly.Adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.revisly.R


class ViewPagerAdapter(private val imageUris: MutableList<String>) : RecyclerView.Adapter<ViewPagerAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ReportImages)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpagerlider, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageUris[position]
        Glide.with(holder.imageView.context)
            .load(imageUri)
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount() = imageUris.size


    // 🔥 Function to update images dynamically
    fun updateImages(newImages: List<String>) {
        if (newImages.isEmpty()) {
            Log.d("ImageAdapter", "No images received, skipping update")
            return
        }

        imageUris.clear()
        imageUris.addAll(newImages)
        notifyDataSetChanged()

        Log.d("ImageAdapter", "Adapter updated with ${imageUris.size} images")
    }


}

