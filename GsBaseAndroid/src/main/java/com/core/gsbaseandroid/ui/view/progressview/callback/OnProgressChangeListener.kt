package com.core.gsbaseandroid.ui.view.progressview.callback

/**  OnProgressChangeListener is an interface for listening to the progress is changed. */
fun interface OnProgressChangeListener {

    /** invoked when progress value is changed. */
    fun onChange(progress: Float)
}
