package com.yieldlab.yieldprobe.events

import com.yieldlab.yieldprobe.data.Bid

/**
 * Event to update notify user about a probe request success.
 * @param bids a HashMap mappping the adslots id to bid objects
 */
class EventProbeSuccess(bids: HashMap<Int, Bid>) {
    var mEventName: String = "EventProbeSuccess"
    var mBids: HashMap<Int, Bid>? = null

    init {
        mBids = bids
    }

    fun getBids(): HashMap<Int, Bid>? {
        return mBids
    }
}
