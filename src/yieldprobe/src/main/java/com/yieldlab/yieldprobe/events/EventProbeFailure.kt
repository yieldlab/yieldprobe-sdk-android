package com.yieldlab.yieldprobe.events

/**
 * Event to update notify user about a probe request failure.
 * @param msg the human readable message
 */
class EventProbeFailure(msg: String?) {
    var mEventName: String = "EventProbeFailure"
    var mMessage: String? = null

    init {
        mMessage = msg
    }

    fun getMessage(): String? {
        return mMessage
    }
}
