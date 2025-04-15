package com.core.gsbaseandroid.utils.extensions

import android.content.Context
import com.core.gsbaseandroid.utils.preferences.GsDefaultConfig

object ContextExtensions {
    val Context.gsDefaultConfig: GsDefaultConfig get() = GsDefaultConfig.newInstance(applicationContext)
}