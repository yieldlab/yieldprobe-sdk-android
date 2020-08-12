package com.yieldlab.yieldprobe.helper

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.yieldlab.yieldprobe.Common
import com.yieldlab.yieldprobe.data.Bid
import com.yieldlab.yieldprobe.data.BidJsonParameter
import com.yieldlab.yieldprobe.events.EventProbeFailure
import com.yieldlab.yieldprobe.events.EventProbeSuccess
import com.yieldlab.yieldprobe.exception.ExceptionNetwork
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray

/**
 * Does the HTTP GET request to the backend.
 * Uses EventBus to communicate back to the SDK user
 * @param timeout set the connection timeout in ms
 */
class NetworkRequestHelper(timeout: Long) {

    /**
     * Instance of the OkHttp Client
     */
    private var mClient: OkHttpClient

    /**
     * init
     */
    init {
        mClient = OkHttpClient().newBuilder()
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            .build()
    }

    /**
     * Does a request to the backend. Will communicate back over EventBus to the SDK user.
     * @param request provide a request object
     */
    fun doHttpGetRequestAsync(
        request: NetworkRequest
    ) {
        val requestBuild = Request.Builder()
            .url(request.mURL)
            .build()

        Common.SDKLog("NetworkRequestHelper: SDK enqueued network request to backend: " + request.mURL)

        mClient.newCall(requestBuild).enqueue(object : Callback {

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onFailure(call: Call, e: IOException) {
                Common.SDKLog("NetworkRequestHelper: onFailure()")

                if (!request.mUseFutures) {
                    // send event back the exception message
                    EventBus.getDefault().post(EventProbeFailure(e.message))
                } else {
                    // complete the Futures
                    request.mFutureBid?.completeExceptionally(ExceptionNetwork(e.message))
                    request.mFutureBids?.completeExceptionally(ExceptionNetwork(e.message))
                }
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call, response: Response) {
                Common.SDKLog("NetworkRequestHelper: onResponse() with code: " + response.code.toString())

                if (response.code == 200) {

                    // parse into JSON array
                    var array: JSONArray
                    try {
                        array = JSONArray(response.body?.string())

                        // convert into hashmap
                        var hashMapAdslotBids: HashMap<Int, Bid> = parseResponse(array)

                        if (!request.mUseFutures) {
                            // send event back to user
                            EventBus.getDefault().post(EventProbeSuccess(hashMapAdslotBids))
                        } else
                            // complete the futures
                            request.mFutureBid?.complete(hashMapAdslotBids.get(request.mAdslotIds?.first()))
                            request.mFutureBids?.complete(hashMapAdslotBids)
                        } catch (e: Exception) {

                        Common.SDKLog("NetworkRequestHelper: Error while parsing response body: " + e.message.toString())

                        if (!request.mUseFutures) {
                            // send event back the exception message
                            EventBus.getDefault().post(EventProbeFailure(e.message))
                        } else {
                            // complete the future, pass ExceptionNetwork with String payload
                            request.mFutureBid?.completeExceptionally(ExceptionNetwork(e.message))
                            request.mFutureBids?.completeExceptionally(ExceptionNetwork(e.message))
                        }
                    }
                } else {
                    if (!request.mUseFutures) {
                        EventBus.getDefault()
                            .post(EventProbeFailure("HTTP Response: " + response.code.toString()))
                    } else {
                        request.mFutureBid?.completeExceptionally(ExceptionNetwork("HTTP Response: " + response.code.toString()))
                        request.mFutureBids?.completeExceptionally(ExceptionNetwork("HTTP Response: " + response.code.toString()))
                    }
                }
            }
        })
    }

    /**
     * Parses a JSON array into a HashMap.
     * @param array provided JSON array
     * @return generated HashMap
     */
    private fun parseResponse(array: JSONArray): HashMap<Int, Bid> {

        var hashMap: HashMap<Int, Bid> = HashMap()

        for (i in 0 until array.length()) {
            val jsonBid = array.getJSONObject(i)

            // fill bid object
            var bid = Bid()

            // settings the id explicitly
            bid.id = jsonBid.getString(BidJsonParameter.JSON_IDENTIFIER_ID)

            // use the GSON library to parse the payload into a MutableMap
            bid.customTargeting = Gson().fromJson(jsonBid.toString(), bid.customTargeting::class.java)

            // GSON maps int values from JSON to double with one digit after decimal point
            // drop last two chars for all values ending with ".0"
            // values will be converted to String with this operation
            for (entry in bid.customTargeting) {
                if (entry.value.toString().endsWith(".0")) {
                    bid.customTargeting[entry.key] = entry.value.toString().dropLast(2)
                }
            }

            // set the timestamp
            bid.timestamp = Common.generateTimeStamp()

            // add the bid
            hashMap.put(Integer.parseInt(bid.id.toString()), bid)
        }

        return hashMap
    }
}
