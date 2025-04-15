package com.core.gsbaseandroid.ui.view.progressview.utils

import android.animation.Animator
import android.animation.Animator.AnimatorListener

@JvmSynthetic
internal fun Animator.doStartAndFinish(start: () -> Unit, finish: () -> Unit) {
    addListener(object : AnimatorListener {
        override fun onAnimationStart(animator: Animator) = start()
        override fun onAnimationEnd(animator: Animator) = finish()
        override fun onAnimationCancel(animator: Animator) = Unit
        override fun onAnimationRepeat(animator: Animator) = Unit
    })
}
