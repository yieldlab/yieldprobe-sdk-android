package com.yieldlab.yieldprobe.exception

/**
 * Exception for network error events.
 */
class ExceptionNetwork(msg: String?) : ExceptionYieldprobe() {
    var mMsg: String? = null

    init {
        mMsg = msg
    }

    fun getMsg(): String? {
        return mMsg
    }
}
