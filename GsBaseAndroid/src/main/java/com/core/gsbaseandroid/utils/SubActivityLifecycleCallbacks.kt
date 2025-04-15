package com.core.gsbaseandroid.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

open class SubActivityLifecycleCallbacks(
    private val callbackCreated: ((Activity) -> Unit)? = null,
    private val callbackStart: ((Activity) -> Unit)? = null,
    private val callbackResumed: ((Activity) -> Unit)? = null
) : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        callbackCreated?.invoke(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        callbackStart?.invoke(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        callbackResumed?.invoke(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        // no-op
    }

    override fun onActivityStopped(activity: Activity) {
        // no-op
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        // no-op
    }
}