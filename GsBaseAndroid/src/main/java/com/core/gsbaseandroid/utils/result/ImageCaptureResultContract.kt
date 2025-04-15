package com.core.gsbaseandroid.utils.result

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import com.core.gsbaseandroid.utils.CoreConstant.CAMERA_FOLDER_PATH
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageCaptureResultContract(private val applicationId: String) : ActivityResultContract<Int, Intent?>() {
    private var currentPhotoPath: String? = null

    override fun createIntent(context: Context, input: Int): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(context, "$applicationId.provider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
            val resolvedIntentActivities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.queryIntentActivities(takePictureIntent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_SYSTEM_ONLY.toLong()))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.packageManager.queryIntentActivities(takePictureIntent, PackageManager.MATCH_SYSTEM_ONLY)
            } else {
                null
            }
            resolvedIntentActivities?.let { resolvedIntent ->
                var packageName: String? = null
                if (resolvedIntent.size > 0) {
                    packageName = resolvedIntent[0].activityInfo.packageName
                }
                packageName?.let {
                    takePictureIntent.setPackage(it)
                }
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? {
        return if (resultCode == Activity.RESULT_OK) {
            var resultIntent = intent
            if (resultIntent == null) {
                resultIntent = Intent()
            }
            resultIntent.putExtra(EXTRA_OUTPUT_PATH, currentPhotoPath)
        } else {
            null
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(CAMERA_FOLDER_PATH)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, "JPEG_${timeStamp}.jpg").apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    companion object {
        const val EXTRA_OUTPUT_PATH = "output_path"
    }
}