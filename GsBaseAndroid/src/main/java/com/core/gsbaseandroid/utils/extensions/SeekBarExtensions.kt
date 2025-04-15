package com.core.gsbaseandroid.utils.extensions

import android.widget.SeekBar

inline fun SeekBar.doOnProgressChanged(crossinline action: (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit) = addOnSeekBarChangeListener(onProgressChanged = action)

inline fun SeekBar.doOnStartTrackingTouch(crossinline action: (seekBar: SeekBar?) -> Unit) = addOnSeekBarChangeListener(onStartTrackingTouch = action)

inline fun SeekBar.doOnStopTrackingTouch(crossinline action: (seekBar: SeekBar?) -> Unit) = addOnSeekBarChangeListener(onStopTrackingTouch = action)

inline fun SeekBar.addOnSeekBarChangeListener(crossinline onProgressChanged: (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit = { _, _, _ -> }, crossinline onStartTrackingTouch: (seekBar: SeekBar?) -> Unit = { _ -> }, crossinline onStopTrackingTouch: (seekBar: SeekBar?) -> Unit = { _ -> }): SeekBar.OnSeekBarChangeListener {
    val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChanged.invoke(seekBar, progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            onStartTrackingTouch.invoke(seekBar)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            onStopTrackingTouch.invoke(seekBar)
        }
    }
    setOnSeekBarChangeListener(seekBarChangeListener)
    return seekBarChangeListener
}