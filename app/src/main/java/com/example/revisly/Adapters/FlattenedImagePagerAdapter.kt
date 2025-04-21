package com.example.revisly.Adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.revisly.Extras.DynamicHeightViewPager2
import com.example.revisly.FlattenedImage
import com.example.revisly.R
import com.github.chrisbanes.photoview.PhotoView

class FlattenedImagePagerAdapter(
    private val flattenedImages: List<FlattenedImage>
) : RecyclerView.Adapter<FlattenedImagePagerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: PhotoView = view.findViewById(R.id.fullImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_full_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flattened = flattenedImages[position]
        val imageUrl = flattened.imageUrl

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(object : CustomTarget<Drawable>() {


                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                holder.imageView.setImageDrawable(resource)

                // Notify height container
                holder.imageView.post {
                    val dynamicContainer = (holder.itemView.parent?.parent as? DynamicHeightViewPager2)
                    dynamicContainer?.measureCurrentView(holder.itemView)
                }





                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })

        }


    override fun getItemCount(): Int = flattenedImages.size
}
