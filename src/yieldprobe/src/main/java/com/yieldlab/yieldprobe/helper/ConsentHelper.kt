package com.yieldlab.yieldprobe.helper

import android.content.Context
import android.preference.PreferenceManager

/**
 * Read out the Consent string.
 * The class uses the proposed identifiers: "IABConsent_CMPPresent" and "IABConsent_ConsentString"
 */
class ConsentHelper {

    /**
     * Read out the consent string.
     * Taken form here: https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Mobile%20In-App%20Consent%20APIs%20v1.0%20Final.md#mobile-in-app-cmp-api-v10-
     * @param context pass a context
     * @return Consent string
     */
    fun getString(context: Context): String? {

        val mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val consentString = mPreferences.getString("IABConsent_ConsentString", "")
        val cmpPresent = mPreferences.getBoolean("IABConsent_CMPPresent", false)

        if (cmpPresent) {
            return consentString
        } else {
            return null
        }
    }
}
