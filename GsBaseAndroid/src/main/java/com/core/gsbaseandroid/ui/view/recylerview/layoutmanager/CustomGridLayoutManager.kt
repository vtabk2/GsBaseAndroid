package com.core.gsbaseandroid.ui.view.recylerview.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class CustomGridLayoutManager(context: Context?, spanCount: Int) : GridLayoutManager(context, spanCount) {
    var isScrollVertically: Boolean = true
    var isScrollHorizontally: Boolean = true

    fun enabledScroll(enabled: Boolean = true) {
        isScrollVertically = enabled
        isScrollHorizontally = enabled
    }

    override fun canScrollVertically(): Boolean {
        return isScrollVertically
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollHorizontally
    }
}