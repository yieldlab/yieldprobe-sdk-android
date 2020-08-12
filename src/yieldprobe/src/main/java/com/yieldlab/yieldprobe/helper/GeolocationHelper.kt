package com.yieldlab.yieldprobe.helper

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yieldlab.yieldprobe.Common
import com.yieldlab.yieldprobe.events.EventGeolocation
import org.greenrobot.eventbus.EventBus

/**
 * Helper class to get a geolocation.
 * Uses a fast and lightweight implementation to get the location over the FusedLocationProviderClient API.
 */
class GeolocationHelper(context: Context) {

    var mFusedLocation: FusedLocationProviderClient? = null
    var mLocation: Location = Location("")
    var mUseDummyLocation = false

    /**
     * init of class
     */
    init {
        mFusedLocation = LocationServices.getFusedLocationProviderClient(context)
        Common.SDKLog("GeolocationHelper: init done")
    }

    /**
     * Just return the last stored location. This call is cheap and fast.
     * @return The last known location. Can be invalid if not updated before (!).
     */
    fun getLocation(): Location? {
        // build a dummy location object
        if (mUseDummyLocation) {
            mLocation.latitude = 53.0
            mLocation.longitude = 9.0
            mLocation.time = System.currentTimeMillis()
            mLocation.accuracy = 100.0f
        }

        return mLocation
    }

    /**
     * Update location with last known location. Function does not have a return statement.
     * Will send an event over EventBus with new location.
     */
    fun updateLocation() {

        mFusedLocation?.lastLocation?.addOnSuccessListener { location: Location? ->

            // From API: Got last known location. In some rare situations this can be null.
            if (location != null) {
                // store location locally
                mLocation = location

                Common.SDKLog("FusedLocation: new location")

                // also send an event with new location
                EventBus.getDefault().post(EventGeolocation(mLocation))

                Common.SDKLog(location.toString())
            } else {
                Common.SDKLog("mFusedLocation: returned null location")
            }
        }
    }
}
