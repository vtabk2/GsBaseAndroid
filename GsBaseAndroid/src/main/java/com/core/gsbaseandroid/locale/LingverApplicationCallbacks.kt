package com.core.gsbaseandroid.locale

import android.content.ComponentCallbacks
import android.content.res.Configuration

internal class LingverApplicationCallbacks(private val callback: (Configuration) -> Unit) : ComponentCallbacks {

    override fun onConfigurationChanged(newConfig: Configuration) {
        callback.invoke(newConfig)
    }

    override fun onLowMemory() {
        // no-op
    }
}
