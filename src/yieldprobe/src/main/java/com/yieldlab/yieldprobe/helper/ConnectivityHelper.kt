package com.yieldlab.yieldprobe.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager

/**
 * Helper class to check connection status amd read out connection type.
 * This code was taken from old sevenonemedia SDK.
 */
class ConnectivityHelper {

    /**
     * Connection types defined in API spec.
     */
    val UNKNOWN = "0"
    val ETHERNET = "1"
    val WIFI = "2"
    val CELLULAR_NETWORK_UNKNOWN_GENERATION = "3"
    val CELLULAR_NETWORK_2G = "4"
    val CELLULAR_NETWORK_3G = "5"
    val CELLULAR_NETWORK_4G = "6"

    /**
     * Check is device is connected.
     * @param context pass a context
     * @return connection status
     */
    fun isConnected(context: Context): Boolean {
        val networkInfo = getNetworkInfo(context)
        return (networkInfo != null && networkInfo.isConnected())
    }

    /**
     * Check type of connection.
     * @param context pass a context
     * @return connection type as encoded in API spec
     */
    fun getConnectionType(context: Context): String {
        val networkInfo = getNetworkInfo(context)

        if (networkInfo != null) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                return WIFI
            } else if (networkInfo.type == ConnectivityManager.TYPE_ETHERNET) {
                return ETHERNET
            } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                when (networkInfo.getSubtype()) {
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_IDEN, // API level 8
                    TelephonyManager.NETWORK_TYPE_CDMA,
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_EDGE -> return CELLULAR_NETWORK_2G
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EHRPD, // API level 11
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_EVDO_B, // API level 9
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_HSPAP, // API level 13
                    17 -> return CELLULAR_NETWORK_3G // TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    TelephonyManager.NETWORK_TYPE_LTE, // API level 11
                    18 -> return CELLULAR_NETWORK_4G // TelephonyManager.NETWORK_TYPE_IWLAN:
                    TelephonyManager.NETWORK_TYPE_UNKNOWN -> return CELLULAR_NETWORK_UNKNOWN_GENERATION
                    else -> return CELLULAR_NETWORK_UNKNOWN_GENERATION
                }
            }
        } else {
            return UNKNOWN
        }
        return UNKNOWN
    }

    /**
     * Get the NetworkInfo object.
     * Please note: used NetworkInfo class is deprecated.
     * @param context pass a context
     * @return NetworkInfo object
     */
    private fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // TODO: deprecated
        return cm.activeNetworkInfo
    }
}
