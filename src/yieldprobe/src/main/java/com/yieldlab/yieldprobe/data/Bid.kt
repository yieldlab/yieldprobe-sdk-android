package com.yieldlab.yieldprobe.data

/**
 * Data class to hold one bid entry.
 */
data class Bid(

    var timestamp: String? = null,
    var id: String? = null,

    // store all other parameters returned here
    var customTargeting: MutableMap<String, Any?> = mutableMapOf<String, Any?>()
)
