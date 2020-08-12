package com.yieldlab.yieldprobe.data

import android.location.Location

/**
 * Data class to hold device meta data.
 * Non valid entries are encoded as null values.
 */
data class DeviceMetaData(
    var mGooglePlayServiceAvailable: Boolean? = null,
    var mIDFA: String? = null,
    var mDeviceType: String? = null,
    var mConnectionType: String? = null,
    var mLocationValid: Boolean = false, // default false (!)
    var mLocation: Location? = null,
    var mAPILevel: Int = 0,
    var mConsent: String? = null
)
