package com.yieldlab.yieldprobe.events

/**
 * Event to update IDFA inside the SDK.
 * @param idfa the IDFA string
 */
class EventIDFA(idfa: String?) {
    var mIDFA: String? = null

    init {
        mIDFA = idfa
    }

    fun getIDFA(): String? {
        return mIDFA
    }
}
