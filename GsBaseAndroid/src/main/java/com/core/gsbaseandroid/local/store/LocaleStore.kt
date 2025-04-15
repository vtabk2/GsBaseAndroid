package com.core.gsbaseandroid.local.store

import java.util.Locale

interface LocaleStore {
    fun getLocale(): Locale
    fun persistLocale(locale: Locale)

    fun setFollowSystemLocale(value: Boolean)
    fun isFollowingSystemLocale(): Boolean
}
