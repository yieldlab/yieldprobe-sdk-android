package com.yieldlab.yieldprobe

import android.location.Location
import com.yieldlab.yieldprobe.data.Configuration
import com.yieldlab.yieldprobe.data.DeviceMetaData
import com.yieldlab.yieldprobe.data.YieldprobeURLParameter
import com.yieldlab.yieldprobe.exception.ExceptionMinAdSlots
import com.yieldlab.yieldprobe.helper.NetworkRequest
import com.yieldlab.yieldprobe.helper.URLBuilderHelper
import java.lang.Exception
import org.junit.Test

class URLBuilderHelperTest {

    var mRequest = NetworkRequest()
    var mMetaData = DeviceMetaData()
    var mConfiguration = Configuration(false, false)

    @Test
    fun emptyAdslots() {
        try {
            mRequest.mAdslotIds = emptySet()
        URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        } catch (e: Exception) {
            // check for right exception
            assert(e::class.java == ExceptionMinAdSlots::class.java)
        }
    }

    @Test
    fun oneAdslot() {
        mRequest.mAdslotIds = mutableSetOf(123456)

        var url = URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        basicURLCheck(url, mRequest.mAdslotIds!!)
    }

    @Test
    fun multipleAdslots() {
        mRequest.mAdslotIds = mutableSetOf(123456, 234234, 234234, 234234)

        var url = URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        basicURLCheck(url, mRequest.mAdslotIds!!)
    }

    @Test
    fun multipleAdslotsNullLocation() {
        mRequest.mAdslotIds = mutableSetOf(123456, 234234, 234234, 234234)
        mMetaData.mLocation = null

        var url = URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        var map = basicURLCheck(url, mRequest.mAdslotIds!!)

        // no lat / lon
        assert(!map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
        assert(!map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
    }

    @Test
    fun multipleAdslotsLocationLatLonZero() {
        mRequest.mAdslotIds = mutableSetOf(123456, 234234, 234234, 234234)
        var loc = Location("")
        loc.latitude = 0.0
        loc.longitude = 0.0
        mMetaData.mLocationValid = true
        mMetaData.mLocation = loc
        mConfiguration.mUsePersonalizedAds = true
        mConfiguration.mGeolocation = true

        var url = URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        var map = basicURLCheck(url, mRequest.mAdslotIds!!)

        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
    }

    @Test
    fun multipleAdslotsLocationLatLonSetPositive() {
        mRequest.mAdslotIds = mutableSetOf(123456, 234234, 234234, 234234)
        var loc = Location("")
        loc.latitude = 54.0
        loc.longitude = 19.0
        mConfiguration.mUsePersonalizedAds = true
        mConfiguration.mGeolocation = true
        mMetaData.mLocationValid = true
        mMetaData.mLocation = loc

        var url = URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        var map = basicURLCheck(url, mRequest.mAdslotIds!!)

        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
    }

    @Test
    fun multipleAdslotsLocationLatLonSetNegative() {
        mRequest.mAdslotIds = mutableSetOf(123456, 234234, 234234, 234234)
        var loc = Location("")
        loc.latitude = -122.0
        loc.longitude = -19.0
        mConfiguration.mUsePersonalizedAds = true
        mConfiguration.mGeolocation = true
        mMetaData.mLocationValid = true
        mMetaData.mLocation = loc

        var url = URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        var map = basicURLCheck(url, mRequest.mAdslotIds!!)

        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
    }

    @Test
    fun multipleAdslotsLocationNonPersonalizedAdsEnabled() {
        mRequest.mAdslotIds = mutableSetOf(123456, 234234, 234234, 234234)
        var loc = Location("")
        loc.latitude = 67.0
        loc.longitude = 19.0
        mConfiguration.mUsePersonalizedAds = false
        mConfiguration.mGeolocation = true
        mMetaData.mLocation = loc

        var url = URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        var map = basicURLCheck(url, mRequest.mAdslotIds!!)

        assert(!map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
        assert(!map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
        assert(!map.containsKey(YieldprobeURLParameter.PARAMETER_YL_RTB_IFA))
    }

    @Test
    fun multipleAdslotsFullySetConfiguration() {
        mRequest.mAdslotIds = mutableSetOf(123456, 234234, 234234, 234234)
        var loc = Location("")
        mConfiguration.mUsePersonalizedAds = true
        mConfiguration.mGeolocation = false
        mConfiguration.mAppName = "appnametest"
        mConfiguration.mBundleName = "bundlenametest"
        mConfiguration.mStoreURL = "http://www.store.de/xyz"
        val hashMapParameterT: HashMap<String, String?> = HashMap()
        hashMapParameterT.put("1", "test")
        hashMapParameterT.put("2", "test")
        hashMapParameterT.put("3", "test")
        mConfiguration.mExtraTargeting = hashMapParameterT
        mMetaData.mLocation = loc
        mMetaData.mIDFA = "abcd-1234-abcd"
        mMetaData.mConnectionType = "2"
        mMetaData.mDeviceType = "4"

        var url = URLBuilderHelper.buildURL(mRequest, mMetaData, mConfiguration)
        var map = basicURLCheck(url, mRequest.mAdslotIds!!)

        assert(!map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
        assert(!map.containsKey(YieldprobeURLParameter.PARAMETER_LAT))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_YL_RTB_IFA))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_PUBAPPNAME))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_PUBBUNDLENAME))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_PUBSTOREURL))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_YL_RTB_CONNECTIONTYPE))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_YL_RTB_DEVICETYPE))
        assert(map.containsKey(YieldprobeURLParameter.PARAMETER_T))
    }

    // return map with parameters after "?"
    private fun basicURLCheck(url: String, adslotSet: Set<Int>): Map<String, String> {

        // valid sample URL
        // https://ad.yieldlab.net/yp/123456?content=json&pvid=1&ts=1572252636956&yl_rtb_devicetype=2&yl_rtb_connectiontype=4&sdk=1

        // check start
        assert(url.startsWith(Common.URL_START))

        // split URL at ? char
        var url_split = url.split("?")
        assert(url_split.size == 2)

        // check for adslots
        // find adslot start
        var adslots = url_split[0].toString().substring(Common.URL_START.length, url_split[0].length)
        var adslot_split = adslots.split(",")
        // check for only integer (empty string not allowed, at least one digit)
        for (id in adslot_split) {
            assert(id.matches("[0-9]+".toRegex()))

            // check if id is in set
            assert(adslotSet.contains(Integer.parseInt(id)))
        }

        // check hard coded: content, pvid, ts and sdk
        var url_payload = url_split[1]
        assert(url_payload.contains("content=json"))
        assert(url_payload.contains("pvid=1"))
        assert(url_payload.contains("sdk=1"))
        assert(url_payload.contains("ts="))

        // split parameters
        var parameters_split = url_payload.split("&")
        // convert into map
        var map = HashMap<String, String>()
        for (parameter in parameters_split) {
            // split at "="
            var parameter_split_2 = parameter.split("=")
            map[parameter_split_2[0]] = parameter_split_2[1]
        }

        return map
    }
}
