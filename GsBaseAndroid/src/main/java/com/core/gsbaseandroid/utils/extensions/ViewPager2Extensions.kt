package com.core.gsbaseandroid.utils.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.findFragmentAtPosition(fragmentManager: FragmentManager, position: Int): Fragment? {
    return fragmentManager.findFragmentByTag("f$position")
}