package com.yieldlab.yieldprobe

import android.content.Context
import android.util.Log
import com.yieldlab.yieldprobe.data.YieldprobeURLParameter
import com.yieldlab.yieldprobe.events.EventProbeLog
import org.greenrobot.eventbus.EventBus

/**
 * Object to hold common data for the SDK. Not all variables and functions are used right now.
 * Still left in code.
 */
object Common {

    /**
     * SDK version for URL
     */
    const val SDK_VERSION = BuildConfig.VERSION_CODE.toString()

    /**
     * Tag for logging messages
     */
    const val TAG_LOG = "YLSDK"

    /**
     * Feature toggle for logging
     */
    const val ENABLE_LOGGING = true

    /**
     * API level required to use CompletableFutures
     */
    const val MIN_API_LEVEL_FOR_FUTURE_CALLS = android.os.Build.VERSION_CODES.N

    /**
     * Constant to configure location mode
     */
    const val WAIT_TIME_FOR_NEW_LOCATION_BEFORE_PROCEED_IN_MS: Long = 10

    /**
     * Log function
     */
    fun SDKLog(msg: String) {
        // eventbus logging set per flag
        if (ENABLE_LOGGING) {
            EventBus.getDefault().post(EventProbeLog(msg))
        }
        // only include Log.d() calls in Debug build
        if (BuildConfig.DEBUG) {
            Log.d(TAG_LOG, msg)
        }
    }

    /**
     * Base URL for HTTP GET request
     */
    const val URL_START = "https://ad.yieldlab.net/yp/"
    const val URL_HARD_CODED = "content=json&pvid=1"
    const val BASE_URL = URL_START + "[" + YieldprobeURLParameter.PARAMETER_ADSLOT + "]?" + URL_HARD_CODED

    /**
     * Bid expires after five minutes. This timeout is set in the Yieldlab backend.
     */
    const val BID_EXPIRE_IN_SECONDS = 5 * 60

    /**
     * Yieldlab API can only process a maximum of ten adslots.
     */
    const val MAX_ADSLOTS = 10

    /**
     * Tablet is above 7.0 inches
     */
    const val MIN_DIAGONAL_FOR_TABLET_IN_INCHES = 7.0

    /**
     * Generate to millis timestamp.
     * @return timestamp String
     */
    fun generateTimeStamp(): String {
        return System.currentTimeMillis().toString()
    }

    /**
     * Get the package name.
     * @param context pass a context
     * @return String with package name
     */
    fun getPackageName(context: Context): String {
        return context.packageName
    }

    /**
     * Get the store URL name
     * @param context pass a context
     * @return String with URL
     */
    fun getStoreURL(context: Context): String {
        return "https://play.google.com/store/apps/details?id=" + getPackageName(context)
    }
}
