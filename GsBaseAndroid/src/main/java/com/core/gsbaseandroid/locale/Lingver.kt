package com.core.gsbaseandroid.locale

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.core.gsbaseandroid.locale.store.LocaleStore
import com.core.gsbaseandroid.locale.store.PreferenceLocaleStore
import java.util.Locale

/**
 * Lingver is a tool to manage your application locale and language.
 *
 * Once you set a desired locale using [setLocale] method, Lingver will enforce your application
 * to provide correctly localized data via [Resources] class.
 */
open class Lingver private constructor(
    private val store: LocaleStore,
    private val delegate: UpdateLocaleDelegate
) {

    internal var systemLocale: Locale = defaultLocale
    internal var systemDeviceLocale: Locale = defaultLocale
    internal var fromUser: Boolean = false

    /**
     * Creates and sets a [Locale] using language, country and variant information.
     *
     * See the [Locale] class description for more information about valid language, country
     * and variant values.
     */
    @JvmOverloads
    fun setLocale(context: Context, language: String, country: String = "", variant: String = "") {
        setLocale(context, Locale(language, country, variant))
    }

    /**
     * Sets a [Locale] which will be used to localize all data coming from [Resources] class.
     *
     * <p>Note that you need to update all already fetched locale-based data manually.
     * [Lingver] is not responsible for that.
     *
     * <p>Note that any call to [setLocale] stops following the system locale and resets
     * [isFollowingSystemLocale] setting.
     */
    fun setLocale(context: Context, locale: Locale) {
        fromUser = true
        store.setFollowSystemLocale(false)
        persistAndApply(context, locale)
    }

    fun setFromUser(fromUser: Boolean) {
        this.fromUser = fromUser
    }

    /**
     * Returns the active [Locale].
     */
    fun getLocale(): Locale {
        return store.getLocale()
    }

    fun getSystemDeviceLocale(): Locale {
        return systemDeviceLocale
    }

    /**
     * Returns a language code which is a part of the active [Locale].
     *
     * Deprecated ISO language codes "iw", "ji", and "in" are converted
     * to "he", "yi", and "id", respectively.
     */
    fun getLanguage(): String {
        return verifyLanguage(getLocale().language)
    }

    /**
     * Applies the system locale and starts following it whenever it changes.
     */
    fun setFollowSystemLocale(context: Context) {
        store.setFollowSystemLocale(true)
        // If Device is running Android 14
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            systemLocale = context.resources.configuration.getLocaleCompat()
        } else {
            systemLocale = systemDeviceLocale
        }
        persistAndApply(context, systemLocale)
    }

    /**
     * Indicates whether the system locale is currently applied.
     */
    fun isFollowingSystemLocale() = store.isFollowingSystemLocale()

    private fun verifyLanguage(language: String): String {
        // get rid of deprecated language tags
        return when (language) {
            "iw" -> "he"
            "ji" -> "yi"
            "in" -> "id"
            else -> language
        }
    }

    internal fun initialize(application: Application) {
        application.registerActivityLifecycleCallbacks(
            LingverActivityLifecycleCallbacks {
                applyForActivity(it)
            }
        )
        application.registerComponentCallbacks(
            LingverApplicationCallbacks {
                processConfigurationChange(application, it)
            }
        )
        val locale = if (store.isFollowingSystemLocale()) {
            systemLocale // might be different on every app launch
        } else {
            store.getLocale()
        }
        persistAndApply(application, locale)
    }

    private fun persistAndApply(context: Context, locale: Locale) {
        store.persistLocale(locale)
        delegate.applyLocale(context, locale)
    }

    private fun applyLocale(context: Context) {
        delegate.applyLocale(context, store.getLocale())
    }

    private fun processConfigurationChange(context: Context, config: Configuration) {
        if (store.isFollowingSystemLocale()) {
            systemLocale = config.getLocaleCompat()
            systemDeviceLocale = config.getLocaleCompat()
            persistAndApply(context, systemLocale)
        } else {
            if (!fromUser) {
                systemDeviceLocale = config.getLocaleCompat()
                fromUser = true
            }
            applyLocale(context)
        }
    }

    private fun applyForActivity(activity: Activity) {
        applyLocale(activity)
        activity.resetTitle()
    }


    companion object {
        @SuppressLint("ConstantLocale")
        private val defaultLocale: Locale = Locale.getDefault()

        private lateinit var instance: Lingver

        /**
         * Returns the global instance of [Lingver] created via init method.
         *
         * @throws IllegalStateException if it was not initialized properly.
         */
        @JvmStatic
        fun getInstance(): Lingver {
            check(::instance.isInitialized) { "Lingver should be initialized first" }
            return instance
        }

        /**
         * Creates and sets up the global instance using a provided language and the default store.
         */
        @JvmStatic
        fun init(application: Application, defaultLanguage: String): Lingver {
            return init(application, Locale(defaultLanguage))
        }

        /**
         * Creates and sets up the global instance using a provided locale and the default store.
         */
        @JvmStatic
        @JvmOverloads
        fun init(application: Application, defaultLocale: Locale = Locale.getDefault()): Lingver {
            return init(application, PreferenceLocaleStore(application, defaultLocale))
        }

        /**
         * Creates and sets up the global instance.
         *
         * This method must be called before any calls to [Lingver] and may only be called once.
         */
        @JvmStatic
        fun init(application: Application, store: LocaleStore): Lingver {
            check(!::instance.isInitialized) { "Already initialized" }
            val lingver = Lingver(store, UpdateLocaleDelegate())
            lingver.initialize(application)
            instance = lingver
            return lingver
        }

        internal fun createInstance(store: LocaleStore, delegate: UpdateLocaleDelegate): Lingver {
            return Lingver(store, delegate)
        }
    }
}
