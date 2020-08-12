package com.yieldlab.yieldprobe.helper

import com.yieldlab.yieldprobe.Common
import com.yieldlab.yieldprobe.data.Configuration
import com.yieldlab.yieldprobe.data.DeviceMetaData
import com.yieldlab.yieldprobe.data.YieldprobeURLParameter
import com.yieldlab.yieldprobe.exception.ExceptionMinAdSlots
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.DecimalFormat

/**
 * Helper class to build an URL.
 */
object URLBuilderHelper {

    /**
     * Build an URL as specified in the API spec..
     * @param request request object
     * @param metaData meta data object
     * @param configuration configuration object
     * @return constructed URL
     */
    fun buildURL(request: NetworkRequest, metaData: DeviceMetaData, configuration: Configuration): String {
        var url = Common.BASE_URL

        // replace adslots
        var sAdslotsPlaceholder = ""
        // test for empty here
        if (request.mAdslotIds!!.isEmpty()) {
            throw ExceptionMinAdSlots()
        } else {
            for (adslot in request.mAdslotIds!!.iterator()) {
                sAdslotsPlaceholder += adslot.toString() + ","
            }
            sAdslotsPlaceholder = sAdslotsPlaceholder.substring(0, sAdslotsPlaceholder.length - 1)
            url = url.replace(
                "[" + YieldprobeURLParameter.PARAMETER_ADSLOT + "]",
                sAdslotsPlaceholder
            )
        }

        // add ts
        url += generateParameter(
            YieldprobeURLParameter.PARAMETER_TS,
            Common.generateTimeStamp()
        )

        // PARAMETER_CONTENT    hard coded with 'json'
        // PARAMETER_PVID       hard coded with '1'

        // optional parameters
        if (configuration.mUsePersonalizedAds) {

            url += addParameter(
                YieldprobeURLParameter.PARAMETER_PUBAPPNAME,
                configuration.mAppName
            )
            url += addParameter(
                YieldprobeURLParameter.PARAMETER_PUBBUNDLENAME,
                configuration.mBundleName
            )
            url += addParameter(
                YieldprobeURLParameter.PARAMETER_PUBSTOREURL,
                configuration.mStoreURL
            )

            // Mobile specific
            // add location only if mGeoLocation is set
            if (configuration.mGeolocation) {

                // identify valid location over flag
                if (metaData.mLocationValid) {

                    // format double to string with decimal point and 12 digits after point
                    val df = DecimalFormat("0.000000000000")

                    url += addParameter(
                        YieldprobeURLParameter.PARAMETER_LAT,
                        df.format(metaData.mLocation?.latitude).toString().replace(",", ".")
                    )
                    url += addParameter(
                        YieldprobeURLParameter.PARAMETER_LON,
                        df.format(metaData.mLocation?.longitude).toString().replace(",", ".")
                    )
                }
            }
            url += addParameter(
                YieldprobeURLParameter.PARAMETER_YL_RTB_IFA,
                metaData.mIDFA
            )
            url += addParameter(
                YieldprobeURLParameter.PARAMETER_YL_RTB_DEVICETYPE,
                metaData.mDeviceType
            )
            url += addParameter(
                YieldprobeURLParameter.PARAMETER_YL_RTB_CONNECTIONTYPE,
                metaData.mConnectionType
            )
        }

        // Video Specific
        /*
        not used for now
        const val PARAMETER_MIN_D = "min_d"
        const val PARAMETER_MAX_D = "max_d"
        const val PARAMETER_STARTDELAY = "startdelay"
        const val PARAMETER_MIMES = "mimes"
        const val PARAMETER_PROTOCOLS = "protocols"
        const val PARAMETER_API = "api"
         */

        // always add consent
        url += addParameter(
            YieldprobeURLParameter.PARAMETER_CONSENT,
            metaData.mConsent
        )

        // parameter T
        url += addParameter(
            YieldprobeURLParameter.PARAMETER_T,
            configuration.mExtraTargeting?.map { (k, v) -> "$k=$v" }?.joinToString("&")
        )

        // always add the SDK version
        url += addParameter(
            YieldprobeURLParameter.PARAMETER_SDK_VERSION,
            Common.SDK_VERSION
        )

        return url
    }

    /**
     * Add a parameter for the URL
     * @param parameter the parameter
     * @param value the value, can be null
     * @return the build parameter if value is not null
     */
    private fun addParameter(parameter: String, value: String?): String {
        if (value != null && value != "null") {
            return generateParameter(
                parameter,
                value
            )
        } else {
            return ""
        }
    }

    /**
     * Generate parameter in the format "&parameter=value"
     * @param parameter the parameter
     * @param value the value, must not be null
     * @return the build parameter in format "&parameter=value"
     */
    private fun generateParameter(parameter: String, value: String): String {
        return "&" + parameter + "=" + encodeParameterURI(value)
    }

    /**
     * Encodes a string URI safe.
     * @param value the value to encode
     * @return the encoded value
     */
    private fun encodeParameterURI(value: String): String {
        var valueEncoded: String = ""
        try {
            valueEncoded = URLEncoder.encode(value, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return valueEncoded
    }
}
