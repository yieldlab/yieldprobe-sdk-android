package com.yieldlab.yieldprobe

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import com.yieldlab.yieldprobe.helper.ConnectivityHelper
import java.io.IOException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetworkInfo

@RunWith(RobolectricTestRunner::class)
class ConnectivityHelperUnitTest {

    var mConnectivityHelper = ConnectivityHelper()
    var mContext = RuntimeEnvironment.systemContext

    private var connectivityManager: ConnectivityManager? = null
    private var shadowConnectivityManager: ShadowConnectivityManager? = null
    private var shadowOfActiveNetworkInfo: ShadowNetworkInfo? = null

    @Before
    @Throws(IOException::class)
    fun setup() {
        connectivityManager = getConnectivityManager()
        shadowConnectivityManager = Shadows.shadowOf(connectivityManager)
        shadowOfActiveNetworkInfo = Shadows.shadowOf(connectivityManager!!.activeNetworkInfo)
    }

    private fun getConnectivityManager(): ConnectivityManager {
        return RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Test
    fun connected() {
        val networkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED,
            ConnectivityManager.TYPE_WIFI,
            0,
            true,
            true
        )
        // setActiveNetworkInfo (instead of setNetworkInfo)
        shadowConnectivityManager!!.setActiveNetworkInfo(networkInfo)

        assert(mConnectivityHelper.isConnected(mContext))
    }

    @Test
    fun notConnected() {
        val networkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED,
            ConnectivityManager.TYPE_WIFI,
            0,
            true,
            false
        )
        // setActiveNetworkInfo (instead of setNetworkInfo)
        shadowConnectivityManager!!.setActiveNetworkInfo(networkInfo)

        assert(!mConnectivityHelper.isConnected(mContext))
    }

    @Test
    fun typeWifi() {
        val networkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED,
            ConnectivityManager.TYPE_WIFI,
            0,
            true,
            false
        )
        shadowConnectivityManager!!.setActiveNetworkInfo(networkInfo)

        assert(mConnectivityHelper.getConnectionType(mContext) == mConnectivityHelper.WIFI)
    }

    @Test
    fun typeCellular2G() {
        val networkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED,
            ConnectivityManager.TYPE_MOBILE,
            TelephonyManager.NETWORK_TYPE_EDGE,
            true,
            true
        )
        shadowConnectivityManager!!.setActiveNetworkInfo(networkInfo)

        assert(mConnectivityHelper.getConnectionType(mContext) == mConnectivityHelper.CELLULAR_NETWORK_2G)
    }

    @Test
    fun typeEthernet() {
        val networkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED,
            ConnectivityManager.TYPE_ETHERNET,
            0,
            true,
            true
        )
        shadowConnectivityManager!!.setActiveNetworkInfo(networkInfo)

        assert(mConnectivityHelper.getConnectionType(mContext) == mConnectivityHelper.ETHERNET)
    }
}
