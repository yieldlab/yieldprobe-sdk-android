package com.yieldlab.yieldprobe.data

/**
 * Object to hold all URL parameters. Will be references in URLBuilderHelper.
 * Parameter taken from document "YLSE-011019-0910-34.pdf".
 */
object YieldprobeURLParameter {

    const val PARAMETER_ADSLOT = "adslots"
    const val PARAMETER_TS = "ts"
    const val PARAMETER_CONTENT = "content" // hard-coded to 'json'
    const val PARAMETER_REDIRECT = "redirect" // not used at all
    const val PARAMETER_PVID = "pvid" // hard-coded with '1'
    const val PARAMETER_PUBREF = "pubref" // not used at all
    const val PARAMETER_PUBAPPNAME = "pubappname"
    const val PARAMETER_PUBBUNDLENAME = "pubbundlename"
    const val PARAMETER_PUBSTOREURL = "pubstoreurl"
    const val PARAMETER_CONSENT = "consent"
    const val PARAMETER_FLOOR = "floor" // not used at all

    // Mobile specific
    const val PARAMETER_LAT = "lat"
    const val PARAMETER_LON = "lon"
    const val PARAMETER_YL_RTB_IFA = "yl_rtb_ifa"
    const val PARAMETER_YL_RTB_DEVICETYPE = "yl_rtb_devicetype"
    const val PARAMETER_YL_RTB_CONNECTIONTYPE = "yl_rtb_connectiontype"

    // Video Specific
    const val PARAMETER_MIN_D = "min_d"
    const val PARAMETER_MAX_D = "max_d"
    const val PARAMETER_STARTDELAY = "startdelay"
    const val PARAMETER_MIMES = "mimes"
    const val PARAMETER_PROTOCOLS = "protocols"
    const val PARAMETER_API = "api"

    // additional parameters
    const val PARAMETER_T = "t"
    const val PARAMETER_SDK_VERSION = "sdk"
}
