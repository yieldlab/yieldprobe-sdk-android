package com.yieldlab.yieldprobe.helper

import android.content.Context
import android.preference.PreferenceManager

/**
 * Read out the Consent string.
 * The class uses the proposed identifiers: "IABTCF_CmpSdkID" and "IABTCF_TCString"
 */
class ConsentHelper {

    /**
     * Read out the consent string.
     * Taken form here: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20CMP%20API%20v2.md#in-app-details
     * @param context pass a context
     * @return Consent string
     */
    fun getString(context: Context): String? {

        val mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val consentString = mPreferences.getString("IABTCF_TCString", "")
        val cmpPresent = mPreferences.getInt("IABTCF_CmpSdkID", 0)

        if (cmpPresent > 0) {
            return consentString
        } else {
            return null
        }
    }
}
