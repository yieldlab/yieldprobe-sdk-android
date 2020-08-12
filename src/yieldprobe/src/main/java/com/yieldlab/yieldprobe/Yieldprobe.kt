package com.yieldlab.yieldprobe

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.yieldlab.yieldprobe.data.Bid
import com.yieldlab.yieldprobe.data.Configuration
import com.yieldlab.yieldprobe.data.DeviceMetaData
import com.yieldlab.yieldprobe.events.EventGeolocation
import com.yieldlab.yieldprobe.events.EventIDFA
import com.yieldlab.yieldprobe.exception.ExceptionLowAPILevel
import com.yieldlab.yieldprobe.exception.ExceptionMaxAdSlots
import com.yieldlab.yieldprobe.exception.ExceptionMinAdSlots
import com.yieldlab.yieldprobe.exception.ExceptionSDKAlreadyInitialized
import com.yieldlab.yieldprobe.exception.ExceptionSDKNotInitialized
import com.yieldlab.yieldprobe.helper.ConnectivityHelper
import com.yieldlab.yieldprobe.helper.ConsentHelper
import com.yieldlab.yieldprobe.helper.DeviceHelper
import com.yieldlab.yieldprobe.helper.GeolocationHelper
import com.yieldlab.yieldprobe.helper.IDFAHelper
import com.yieldlab.yieldprobe.helper.NetworkRequest
import com.yieldlab.yieldprobe.helper.NetworkRequestHelper
import com.yieldlab.yieldprobe.helper.URLBuilderHelper
import java.util.concurrent.CompletableFuture
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Yieldprobe is implemented as singleton. Using Kotlin language object feature.
 * All methods are annotated with @JvmStatic so they are easy callable from Java.
 */
@SuppressLint("StaticFieldLeak")
object Yieldprobe {

    /**
     * Default HTTP connection timeout
     */
    const val DEFAULT_HTTP_CONNECTION_TIMEOUT_IN_MS: Long = 10000

    /**
     * Used to register EventBus.
     */
    init {
        EventBus.getDefault().register(this)
    }

    /**
     * Flag for current SDK status.
     */
    private var mIsSDKInitialized: Boolean = false

    /**
     * Stored application context
     */
    @SuppressLint("StaticFieldLeak")
    private lateinit var mContext: Context

    /**
     * Current configuration
     */
    private lateinit var mConfiguration: Configuration

    /**
     * Instances of all helper classes.
     */
    private var mDeviceHelper: DeviceHelper = DeviceHelper()
    private var mConnectivityHelper: ConnectivityHelper = ConnectivityHelper()
    private lateinit var mGeolocationHelper: GeolocationHelper
    private var mIDFAHelper: IDFAHelper = IDFAHelper()
    private lateinit var mNetworkRequestHelper: NetworkRequestHelper
    private var mConsentHelper: ConsentHelper = ConsentHelper()

    /**
     * The current device meta data.
     * Helper classes will set members.
     * URL builder will get values from here.
     */
    private var mDeviceMetaData: DeviceMetaData = DeviceMetaData()

    /**
     * Initializes the SDK. Has to be called once for the lifetime of the application.
     * @param context pass the ApplicationContext (!)
     * @param activity pass an Activity
     * @param configuration pass a configuration
     */
    @JvmStatic
    fun initialize(context: Context, activity: Activity, configuration: Configuration) {

        // handle multiple initialize calls
        if (!mIsSDKInitialized) {
            mContext = context
            mConfiguration = configuration

            // geolocation helper can be initialized without location permission
            mGeolocationHelper = GeolocationHelper(mContext)

            // read out and store API level
            mDeviceMetaData.mAPILevel = Build.VERSION.SDK_INT

            // update IDFA and geolocation
            updateDeviceMetaData(context, activity)

            // create NetworkRequestHelper
            mNetworkRequestHelper = NetworkRequestHelper(configuration.mRequestTimeoutInMs)

            // SDK is now ready, set flag
            mIsSDKInitialized = true

            Common.SDKLog("SDK initialize() successful.")
        } else {
            Common.SDKLog("SDK initialize() unsuccessful. Already initialized().")
            throw ExceptionSDKAlreadyInitialized()
        }
    }

    /**
     * EventBus function for EventIDFA.
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventIDFA) {
        mDeviceMetaData.mIDFA = event.getIDFA()
    }

    /**
     * EventBus function for EventGeolocation.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventGeolocation) {
        mDeviceMetaData.mLocationValid = true
        mDeviceMetaData.mLocation = event.getLocation()
    }

    /**
     * Check the SDK initialize status.
     * @return Boolean
     */
    @JvmStatic
    fun isInitialized(): Boolean {
        return mIsSDKInitialized
    }

    /**
     * Get SDK version. Can be called without initializing SDK before.
     * @return String with version
     */
    @JvmStatic
    fun getVersion(): String {
        return Common.SDK_VERSION
    }

    /**
     * Get version name (e.g. "1.0.0"). Can be called without initializing SDK before.
     * @return String with version name
     */
    @JvmStatic
    fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    /**
     * Sets a new configuration.
     * @param context pass the ApplicationContext (!)
     * @param activity pass an Activity
     * @param configuration pass a configuration
     */
    @JvmStatic
    fun configure(context: Context, activity: Activity, configuration: Configuration) {
        if (mIsSDKInitialized) {
            mConfiguration = configuration

            // update IDFA and geolocation
            updateDeviceMetaData(context, activity)

            // update network request helper
            mNetworkRequestHelper = NetworkRequestHelper(configuration.mRequestTimeoutInMs)

            Common.SDKLog("SDK configure() successful.")
        } else {
            Common.SDKLog("SDK not configured yet.")
            throw ExceptionSDKNotInitialized()
        }
    }

    /**
     * Internal function to update device meta data based on current configuration.
     * @param context pass the ApplicationContext (!)
     * @param activity pass an Activity
     */
    private fun updateDeviceMetaData(context: Context, activity: Activity) {

        // only read out meta data if allowed from configuration
        if (mConfiguration.mUsePersonalizedAds) {

            // set device type
            mDeviceMetaData.mDeviceType = mDeviceHelper.getDeviceType(context, activity)

            // check Play Services. Do not show a popup to user
            if (isGooglePlayServicesAvailable(activity, false)) {
                // set flag
                mDeviceMetaData.mGooglePlayServiceAvailable = true

                // get the IDFA
                mIDFAHelper.getIDFA(context)

                if (mConfiguration.mGeolocation) {
                    // update geolocation
                    mGeolocationHelper.updateLocation()
                } else {
                    // reset flag and location object
                    mDeviceMetaData.mLocationValid = false
                    mDeviceMetaData.mLocation = null
                }
            } else {
                // update flag
                mDeviceMetaData.mGooglePlayServiceAvailable = false
                Common.SDKLog("updateDeviceMetaData(): isGooglePlayServicesAvailable false")
            }
        } else {
            // reset meta data
            mDeviceMetaData.mConnectionType = null
            mDeviceMetaData.mDeviceType = null
            mDeviceMetaData.mIDFA = null
            mDeviceMetaData.mLocationValid = false
            mDeviceMetaData.mLocation = null
        }
    }

    /**
     * Checking if Google Player Service is available.
     * This call also can be done with SDK not initialized.
     * @param activity pass an Activity
     * @param showPopup show a user popup to enable services.
     * @return Boolean if Google Player Service is available
     */
    @JvmStatic
    fun isGooglePlayServicesAvailable(activity: Activity, showPopup: Boolean): Boolean {

        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)

        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                Common.SDKLog("isGooglePlayServicesAvailable(): isUserResolvableError")
                // check for popup flag
                if (showPopup) {
                    // this will show a system dialog to notify  user
                    // a button to enable play services is provided
                    googleApiAvailability.getErrorDialog(activity, status, 2404).show()
                }
            }
            return false
        }
        return true
    }

    /**
     * Gets the current SDK configuration.
     * @return copy of current configuration state.
     */
    @JvmStatic
    fun getConfiguration(): Configuration {
        if (mIsSDKInitialized) {
            Common.SDKLog("SDK getConfiguration() successful.")

            // create a deep copy (!)
            var configurationReturn = mConfiguration.copy()
            configurationReturn.mExtraTargeting = mConfiguration.mExtraTargeting?.toMap()

            return configurationReturn
        } else {
            Common.SDKLog("SDK not configured yet.")
            throw ExceptionSDKNotInitialized()
        }
    }

    /**
     * Gets the current device meta data.
     * @return copy of current device meta data state.
     */
    @JvmStatic
    fun getDeviceMetaData(): DeviceMetaData {
        if (mIsSDKInitialized) {
            Common.SDKLog("SDK getDeviceMetaData() successful.")

            // only primitive types. Default copy() operation is enough for a deep copy.
            return mDeviceMetaData.copy()
        } else {
            Common.SDKLog("SDK not configured yet.")
            throw ExceptionSDKNotInitialized()
        }
    }

    /**
     * Call the backend for one adslot.
     * @param adslotId one Integer adslot
     */
    @JvmStatic
    fun probeWithEvents(
        adslotId: Int
    ) {
        if (mIsSDKInitialized) {
            // delegate call to the general function
            probeWithEvents(setOf(adslotId))
        } else {
            Common.SDKLog("SDK not configured yet.")
            throw ExceptionSDKNotInitialized()
        }
    }

    /**
     * Call the backend for multiple adslot.
     * @param adslotIds provide a Set of Int
     */
    @JvmStatic
    fun probeWithEvents(
        adslotIds: Set<Int>
    ) {
        if (mIsSDKInitialized) {
            // create a new request object
            var newRequest = NetworkRequest(adslotIds)

            doProbeRequest(newRequest)
        } else {
            Common.SDKLog("SDK not configured yet.")
            throw ExceptionSDKNotInitialized()
        }
    }

    /**
     * Call the backend for one adslot.
     * @param adslotId one Integer adslot
     * @return CompletableFuture with result
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun probe(adslotId: Int):
            CompletableFuture<Bid> {
        if (mIsSDKInitialized) {
            // do a runtime check for API level here
            if (mDeviceMetaData.mAPILevel >= Common.MIN_API_LEVEL_FOR_FUTURE_CALLS) {
                val futureBid = CompletableFuture<Bid>()

                // create a new request object
                var newRequest =
                    NetworkRequest(setOf(adslotId), futureBid, null)

                doProbeRequest(newRequest)
                return futureBid
            } else {
                // throw exception, return nothing
                throw ExceptionLowAPILevel()
            }
        } else {
            Common.SDKLog("SDK not configured yet.")
            throw ExceptionSDKNotInitialized()
        }
    }

    /**
     * Call the backend for multiple adslot.
     * @param adslotIds provide a Set of Int
     * @return CompletableFuture with result
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun probe(adslotIds: Set<Int>):
            CompletableFuture<HashMap<Int, Bid>> {
        if (mIsSDKInitialized) {
            // do a runtime check for API level here
            if (mDeviceMetaData.mAPILevel >= Common.MIN_API_LEVEL_FOR_FUTURE_CALLS) {

                val futureBids = CompletableFuture<HashMap<Int, Bid>>()

                // create a new request object
                var newRequest =
                    NetworkRequest(adslotIds, null, futureBids)

                doProbeRequest(newRequest)
                return futureBids
            } else {
                // throw exception, return nothing
                throw ExceptionLowAPILevel()
            }
        } else {
            Common.SDKLog("SDK not configured yet.")
            throw ExceptionSDKNotInitialized()
        }
    }

    /**
     * Internal function to trigger a request.
     * @param request object
     */
    private fun doProbeRequest(
        request: NetworkRequest
    ) {

        if (mIsSDKInitialized) {

            // get connection status
            if (mConnectivityHelper.isConnected(mContext)) {

                // getting connection type for each request
                mDeviceMetaData.mConnectionType = mConnectivityHelper.getConnectionType(mContext)

                // check adslot parameters max
                if (request.mAdslotIds!!.size > Common.MAX_ADSLOTS) {
                    throw ExceptionMaxAdSlots()
                }

                // check for no adslots provided
                if (request.mAdslotIds!!.isEmpty()) {
                    throw ExceptionMinAdSlots()
                }

                // read out for every request
                mDeviceMetaData.mConsent = mConsentHelper.getString(mContext)

                // get new geolocation based on current configuration
                if (mConfiguration.mUsePersonalizedAds && mConfiguration.mGeolocation) {
                    // this will trigger a new location fetch (updated over EventBus)
                    mGeolocationHelper.updateLocation()

                    // Wait before getting the location, then proceed the request.
                    // In almost all cases the small wait time is enough for a new location update
                    // from FusedLocation provider.
                    val h = Handler()
                    h.postDelayed({
                            mDeviceMetaData.mLocation = mGeolocationHelper.getLocation()
                            doProbeRequestProceed(request)
                    }, Common.WAIT_TIME_FOR_NEW_LOCATION_BEFORE_PROCEED_IN_MS)
                } else {
                    // proceed immediately
                    doProbeRequestProceed(request)
                }
            } else {
                // Not connected. Do nothing.
            }
        } else {
            throw ExceptionSDKNotInitialized()
        }
    }

    /**
     * Internal function to proceed with a request.
     * @param request object
     */
    private fun doProbeRequestProceed(request: NetworkRequest) {
        // build URL with meta data and set in request object
        var url = URLBuilderHelper.buildURL(request, mDeviceMetaData, mConfiguration)
        request.mURL = url

        // enqueue request to server
        mNetworkRequestHelper.doHttpGetRequestAsync(request)
    }
}
