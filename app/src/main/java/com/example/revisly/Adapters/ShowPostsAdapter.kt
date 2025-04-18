package com.example.revisly.Adapters

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.example.revisly.Posts
import com.example.revisly.R
import com.bumptech.glide.request.target.CustomTarget
import kotlin.math.min

class ShowPostsAdapter(private val list: List<Posts>) : RecyclerView.Adapter<ShowPostsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.ImageSet)
    }

    private val animatedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_showpposts, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

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

        // Detect orientation
        val orientation = holder.itemView.resources.configuration.orientation

        // Set scaleType to MATRIX for custom transformation
        holder.img.scaleType = ImageView.ScaleType.MATRIX

        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(item.img)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val originalWidth = resource.width
                    val originalHeight = resource.height

                    val imageViewWidth = holder.img.width.takeIf { it > 0 }
                        ?: (holder.img.resources.displayMetrics.widthPixels / 3)

                    val aspectRatio = originalHeight.toFloat() / originalWidth
                    var targetHeight = (imageViewWidth * aspectRatio).toInt()

                    // Apply max height based on orientation
                    val maxHeight = if (orientation == Configuration.ORIENTATION_PORTRAIT) 1000 else 800
                    targetHeight = min(targetHeight, maxHeight)

                    holder.img.layoutParams.height = targetHeight
                    holder.img.requestLayout()

                    // Apply Top-Crop Transformation
                    val matrix = Matrix()
                    val scale = imageViewWidth.toFloat() / originalWidth
                    matrix.setScale(scale, scale) // Scale to fit width
                    holder.img.imageMatrix = matrix

                    holder.img.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = position
}
