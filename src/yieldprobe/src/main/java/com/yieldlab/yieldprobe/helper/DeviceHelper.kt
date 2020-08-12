package com.yieldlab.yieldprobe.helper

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.Context.UI_MODE_SERVICE
import android.content.res.Configuration
import android.util.DisplayMetrics
import com.yieldlab.yieldprobe.Common

/**
 * Get the device type. Identifier encoding is taken from Yieldlab API spec.
 * Please note:
 */
class DeviceHelper {

    /**
     * Device type as encoded in the API spec.
     */
    val DEVICE_UNKNOWN = "UNKNOWN"
    val DEVICE_CONNECTED_TV = "3"
    val DEVICE_TABLET = "5"
    val DEVICE_PHONE = "4"

    /**
     * Get device type.
     * Please note: This functions needs a reference to a Context as well as to an Activity.
     * @param context pass a context
     * @param activity pass an activity
     * @return device type as encoded in the API spec.
     */
    fun getDeviceType(context: Context, activity: Activity): String {
        return if (checkIsTV(context)) {
            DEVICE_CONNECTED_TV
        } else if (checkIsTablet(activity)) {
            DEVICE_TABLET
        } else {
            DEVICE_PHONE
        }
    }

    /**
     * Check if current device is a tablet. Using screen diagonal as identifier.
     * Every device above 7.0 inches is a tablet.
     * @param activity pass an activity
     * @return Boolean if device is a tablet
     */
    private fun checkIsTablet(activity: Activity): Boolean {
        val display = activity.windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)

        val widthInches = metrics.widthPixels / metrics.xdpi
        val heightInches = metrics.heightPixels / metrics.ydpi
        val diagonalInches = Math.sqrt(
            Math.pow(widthInches.toDouble(), 2.0) + Math.pow(heightInches.toDouble(), 2.0)
        )
        return (diagonalInches >= Common.MIN_DIAGONAL_FOR_TABLET_IN_INCHES)
    }

    /**
     * See here: https://developer.android.com/training/tv/start/hardware.html#runtime-check
     * @param context pass a context
     * @return Boolean is device is a TV
     */
    private fun checkIsTV(context: Context): Boolean {
        val uiModeManager = context.getSystemService(UI_MODE_SERVICE) as UiModeManager
        return (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION)
    }
}
