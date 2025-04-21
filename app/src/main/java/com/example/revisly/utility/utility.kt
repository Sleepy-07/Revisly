package com.example.revisly.utility

import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.canScrollHorizontallyBackward(): Boolean {
    return this.currentItem > 0
}

fun ViewPager2.canScrollHorizontallyForward(): Boolean {
    return this.adapter?.let { currentItem < it.itemCount - 1 } ?: false
}
