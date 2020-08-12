package com.yieldlab.yieldprobe.events

import android.location.Location

/**
 * Event to update Geolocation inside the SDK.
 * @param geolocation the new location object
 */
class EventGeolocation(geolocation: Location?) {
    var mGeolocation: Location? = null

    init {
        mGeolocation = geolocation
    }

    fun getLocation(): Location? {
        return mGeolocation
    }
}
