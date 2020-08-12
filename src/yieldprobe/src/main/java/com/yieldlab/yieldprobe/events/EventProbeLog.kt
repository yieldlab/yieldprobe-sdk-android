package com.yieldlab.yieldprobe.events

/**
 * Event to update notify user about a log event.
 * @param msg the human readable message
 */
class EventProbeLog(msg: String?) {
    var mEventName: String = "EventProbeLog"
    var mMessage: String? = null

    init {
        mMessage = msg
    }

    fun getMessage(): String? {
        return mMessage
    }
}
