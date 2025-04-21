package com.example.revisly.Extras

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class DynamicHeightViewPager2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var currentView: View? = null

    fun measureCurrentView(view: View) {
        currentView = view
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        currentView?.let {
            it.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            height = it.measuredHeight
        }
        if (height == 0) height = MeasureSpec.getSize(heightMeasureSpec)
        val newHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }
}