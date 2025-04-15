package com.core.gsbaseandroid.ui.view.progressview.callback

/**  OnProgressClickListener is an interface for listening to the progress bar is clicked. */
fun interface OnProgressClickListener {

    /** invoked when the progress bar is touched. */
    fun onClickProgress(highlighting: Boolean)
}
