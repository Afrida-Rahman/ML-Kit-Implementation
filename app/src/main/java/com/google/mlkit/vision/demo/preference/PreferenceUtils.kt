package com.google.mlkit.vision.demo.preference

import android.content.Context
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import com.google.common.base.Preconditions
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions

/**
 * Utility class to retrieve shared preferences.
 */
object PreferenceUtils {
    @RequiresApi(VERSION_CODES.LOLLIPOP)
    fun getCameraXTargetResolution(context: Context, lensFacing: Int): Size? {
        Preconditions.checkArgument(
            lensFacing == CameraSelector.LENS_FACING_BACK
                    || lensFacing == CameraSelector.LENS_FACING_FRONT
        )
        val prefKey = if (lensFacing == CameraSelector.LENS_FACING_BACK) "crctas" else "cfctas"

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            Size.parseSize(sharedPreferences.getString(prefKey, null))
        } catch (e: Exception) {
            null
        }
    }

    fun getPoseDetectorOptionsForLivePreview(context: Context): PoseDetectorOptionsBase {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val preferGPU = sharedPreferences.getBoolean("pdpg", true)
        val builder = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        if (preferGPU) {
            builder.setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU)
        }
        return builder.build()
    }
}
