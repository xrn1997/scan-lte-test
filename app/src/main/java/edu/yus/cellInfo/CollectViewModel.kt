package edu.yus.cellInfo

import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel

/**
 *
 * @author xrn1997
 * @date 2022/3/31 19:28.
 */
class CollectViewModel : ViewModel() {
    /**
     * 启停扫描wifi
     */
    var isScanWifi = false

    fun startScanWifi() {
        Thread {
            CollectModel.registerWifi()
            while (isScanWifi) {
                CollectModel.scanWifi()
                Thread.sleep(1000)
            }
            CollectModel.unregisterWifi()
        }.start()
    }

    @Suppress("DEPRECATION")
    fun startScanTLE(listener: MyPhoneStateListener?) {
        if (listener != null) {
            CollectModel.scanLTE(listener)
        }
    }

    @Suppress("DEPRECATION")
    fun stopScanTLE(listener: PhoneStateListener?) {
        if (listener != null) {
            CollectModel.stopScanLTE(listener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun startScanTLE(telephonySignalCallBack: TelephonyCallback?) {
        if (telephonySignalCallBack != null) {
            CollectModel.scanLTE(telephonySignalCallBack)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun stopScanTLE(telephonySignalCallBack: TelephonyCallback?) {
        if (telephonySignalCallBack != null) {
            CollectModel.stopScanLTE(telephonySignalCallBack)
        }
    }
}