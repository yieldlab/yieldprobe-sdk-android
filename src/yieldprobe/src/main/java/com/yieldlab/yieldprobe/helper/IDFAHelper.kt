package com.yieldlab.yieldprobe.helper

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.yieldlab.yieldprobe.Common
import com.yieldlab.yieldprobe.events.EventIDFA
import org.greenrobot.eventbus.EventBus

/**
 * Helper class to read out out the IDFA from Google. Also known as Google Advertiser ID.
 * Using this library: com.google.android.gms:play-services-ads-identifier:17.0.0
 */
class IDFAHelper {

    /**
     * Get current IDFA. Check if Google Play Services must be present (!)
     * @param context pass a context
     */
    fun getIDFA(context: Context) {
        Common.SDKLog("IDFAHelper: initialize() done")
        IDFATask().execute(context)
    }

    /**
     * IDFA must not be read on UI-Thread. Using an AsyncTask.
     * Task will send an event over EventBus with IDFA string.
     */
    class IDFATask() : AsyncTask<Context, Void, String>() {
        override fun doInBackground(vararg params: Context): String? {

            var adInfo: AdvertisingIdClient.Info? = null

            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(params[0])
            } catch (e: Exception) {
                Common.SDKLog("IDFAHelper: IDFATask() " + e.message)
                e.printStackTrace()
            }

            return adInfo?.id
        }

        override fun onPostExecute(result: String?) {
            // send event for IDFA
            EventBus.getDefault().post(EventIDFA(result))
        }
    }
}
