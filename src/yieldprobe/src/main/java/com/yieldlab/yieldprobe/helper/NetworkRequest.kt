package com.yieldlab.yieldprobe.helper

import com.yieldlab.yieldprobe.data.Bid
import java.util.concurrent.CompletableFuture

/**
 * Class to hold all data for one network request.
 * Can not be implemented as data class.
 * Future variable can not be referenced on lower API levels (!)
 */
class NetworkRequest {

    /**
     * init
     */
    init {
        //
    }

    /**
     * default constructor used in unit tests.
     */
    constructor() {
        //
    }

    /**
     * constructor for requests with EventBus.
     */
    constructor(adslotIds: Set<Int>?) {
        mAdslotIds = adslotIds
        mUseFutures = false
    }

    /**
     * constructor for request with Futures.
     */
    constructor(
        adslotIds: Set<Int>?,
        futureBid: CompletableFuture<Bid>?,
        futureBids: CompletableFuture<HashMap<Int, Bid>>?
    ) {
        mAdslotIds = adslotIds
        mUseFutures = true
        mFutureBid = futureBid
        mFutureBids = futureBids
    }

    /**
     * Identifier for the request. Not used right now.
     * Could be used to remove request from queue.
     */
    var mId: String? = null

    var mAdslotIds: Set<Int>? = null
    var mURL: String = ""

    /**
     * If this variable is unset EventBus will be used. Otherwise Futures.
     */
    var mUseFutures: Boolean = false

    /**
     * Do not reference these variables on low API levels (!).
     */
    var mFutureBid: CompletableFuture<Bid>? = null
    var mFutureBids: CompletableFuture<HashMap<Int, Bid>>? = null
}
