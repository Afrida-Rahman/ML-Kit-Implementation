package com.google.mlkit.vision.demo.preference

import android.content.Context
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.camera.core.CameraSelector
import com.google.common.base.Preconditions
import com.google.mlkit.vision.demo.R
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

/**
 * Utility class to retrieve shared preferences.
 */
object PreferenceUtils {
    private const val POSE_DETECTOR_PERFORMANCE_MODE_FAST = 1

    @RequiresApi(VERSION_CODES.LOLLIPOP)
    fun getCameraXTargetResolution(context: Context, lensFacing: Int): Size? {
        Preconditions.checkArgument(
            lensFacing == CameraSelector.LENS_FACING_BACK
                    || lensFacing == CameraSelector.LENS_FACING_FRONT
        )
        val prefKey =
            if (lensFacing == CameraSelector.LENS_FACING_BACK) context.getString(R.string.pref_key_camerax_rear_camera_target_resolution) else context.getString(
                R.string.pref_key_camerax_front_camera_target_resolution
            )
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            Size.parseSize(sharedPreferences.getString(prefKey, null))
        } catch (e: Exception) {
            null
        }
    }

    fun getPoseDetectorOptionsForLivePreview(context: Context): PoseDetectorOptionsBase {
        val performanceMode = getModeTypePreferenceValue(
            context,
            R.string.pref_key_live_preview_pose_detection_performance_mode,
            POSE_DETECTOR_PERFORMANCE_MODE_FAST
        )
        val preferGPU = preferGPUForPoseDetection(context)
        return if (performanceMode == POSE_DETECTOR_PERFORMANCE_MODE_FAST) {
            val builder =
                PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            if (preferGPU) {
                builder.setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU)
            }
            builder.build()
        } else {
            val builder = AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            if (preferGPU) {
                builder.setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU)
            }
            builder.build()
        }
    }

    private fun preferGPUForPoseDetection(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_pose_detector_prefer_gpu)
        return sharedPreferences.getBoolean(prefKey, true)
    }

    fun shouldSegmentationEnableRawSizeMask(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_segmentation_raw_size_mask)
        return sharedPreferences.getBoolean(prefKey, false)
    }

    /**
     * Mode type preference is backed by [android.preference.ListPreference] which only support
     * storing its entry value as string type, so we need to retrieve as string and then convert to
     * integer.
     */
    private fun getModeTypePreferenceValue(
        context: Context, @StringRes prefKeyResId: Int, defaultValue: Int
    ): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(prefKeyResId)
        return sharedPreferences.getString(prefKey, defaultValue.toString())!!.toInt()
    }
}