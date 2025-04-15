package com.core.gsbaseandroid.utils.extensions

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import androidx.core.os.BundleCompat

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getParcelableExtra(key, T::class.java)
    } else {
        this.getParcelableExtra(key) as? T
    }
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.getBundle(bundleKey: String, key: String): T? {
    return getBundleExtra(bundleKey)?.let { bundle ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            BundleCompat.getParcelable(bundle, key, T::class.java)
        } else {
            bundle.getParcelable(key)
        }
    }
}