package com.core.gsbaseandroid.utils

import android.os.Environment
import java.io.File

object CoreConstant {
    const val ELEVATION_RANK_1 = 10f
    const val ELEVATION_RANK_2 = 12f
    const val ELEVATION_RANK_3 = 14f
    const val ELEVATION_RANK_4 = 16f

    const val LIMIT_LOADED = 100

    const val CAMERA_FOLDER = "Camera"
    var CAMERA_FOLDER_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + File.separator + CAMERA_FOLDER

    const val GOOGLE_PHOTOS = "Google Photos"
    const val GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos"
}