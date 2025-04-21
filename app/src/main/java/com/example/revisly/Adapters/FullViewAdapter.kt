package com.example.revisly.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.revisly.R

class FullscreenImageAdapter(private val images: List<String>?) :
    RecyclerView.Adapter<FullscreenImageAdapter.FullscreenImageViewHolder>() {

    class FullscreenImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.fullscreen_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullscreenImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fullscreen_images, parent, false)
        return FullscreenImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: FullscreenImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(images?.get(position))
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = images?.size ?: 0
}