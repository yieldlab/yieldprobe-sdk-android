package com.yieldlab.yieldprobe.data

import com.yieldlab.yieldprobe.Yieldprobe

/**
 * Data class to hold the SDK configuration.
 */
data class Configuration(
    var mGeolocation: Boolean = false,
    var mUsePersonalizedAds: Boolean = true,

    // default connection timeout for HTTP request
    var mRequestTimeoutInMs: Long = Yieldprobe.DEFAULT_HTTP_CONNECTION_TIMEOUT_IN_MS,

    var mAppName: String? = null,
    var mBundleName: String? = null,
    var mStoreURL: String? = null,

    // additional parameter
    var mExtraTargeting: Map<String, String?>? = null
)
