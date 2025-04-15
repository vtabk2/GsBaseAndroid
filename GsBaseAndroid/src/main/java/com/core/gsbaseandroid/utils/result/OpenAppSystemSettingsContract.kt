package com.core.gsbaseandroid.utils.result

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi

class OpenAppSystemSettingsContract(private val applicationId: String) : ActivityResultContract<Int, Int>() {
    private var input: Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun createIntent(context: Context, input: Int): Intent {
        this.input = input
        return Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", applicationId, null)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return input
    }
}