package com.example.revisly

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class TopCropImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        drawable?.let {
            val matrix = imageMatrix
            val scale: Float

            val viewWidth = width - paddingLeft - paddingRight
            val viewHeight = height - paddingTop - paddingBottom
            val drawableWidth = it.intrinsicWidth
            val drawableHeight = it.intrinsicHeight

            if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
                scale = viewHeight.toFloat() / drawableHeight.toFloat()
            } else {
                scale = viewWidth.toFloat() / drawableWidth.toFloat()
            }

            val dx = 0f
            val dy = 0f // 👈 This is what keeps the crop anchored at the top

            matrix.setScale(scale, scale)
            matrix.postTranslate(dx, dy)

            imageMatrix = matrix
        }

        return super.setFrame(l, t, r, b)
    }
}