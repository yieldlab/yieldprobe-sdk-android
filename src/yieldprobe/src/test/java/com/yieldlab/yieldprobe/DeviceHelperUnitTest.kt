package com.yieldlab.yieldprobe

import android.app.Activity
import com.yieldlab.yieldprobe.helper.DeviceHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DeviceHelperUnitTest {

    var mDeviceHelperUnitTest = DeviceHelper()
    var mContext = RuntimeEnvironment.systemContext
    var mActivity = Activity()

    @Test
    fun deviceType() {
        mActivity = Robolectric.buildActivity(Activity::class.java).create().resume().get()
        var type = mDeviceHelperUnitTest.getDeviceType(mContext, mActivity)
        assert(
            type == mDeviceHelperUnitTest.DEVICE_CONNECTED_TV ||
                    type == mDeviceHelperUnitTest.DEVICE_PHONE ||
                    type == mDeviceHelperUnitTest.DEVICE_TABLET ||
                    type == mDeviceHelperUnitTest.DEVICE_UNKNOWN
        )
    }
}
