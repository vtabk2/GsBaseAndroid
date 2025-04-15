package com.core.gsbaseandroid.utils.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeUntil(
    owner: LifecycleOwner,
    predicate: (T) -> Boolean,
    observer: (T) -> Unit
) {
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            if (predicate(value)) {
                removeObserver(this)
            }
            observer(value)
        }
    })
}