package edu.yus.cellInfo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import edu.yus.cellInfo.MyApplication.Companion.context
import java.io.OutputStream
import java.util.*


/**
 * Model
 * @author xrn1997
 * @date 2022/3/30 19:45
 */
object CollectModel {
    private val telephonyManager = context.applicationContext
        .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private val wifiManager: WifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val wifiUri =
        FileUtils.getFileUri(context, "wifi-${Calendar.getInstance().time}.csv", "text/csv")
    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            val os = wifiUri?.let { MyApplication.context.contentResolver.openOutputStream(it, "wa") }
            if (success) {
                scanSuccess(os)
            } else {
                scanFailure()
            }
        }
    }

    /**
     * 注册
     */
    fun registerWifi() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
    }

    /**
     * 扫描Wifi设备
     */
    @Suppress("DEPRECATION")
    fun scanWifi() {
        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure()
        }

    }

    /**
     * wifi扫描成功
     */
    private fun scanSuccess(os: OutputStream?) {
        val list = wifiManager.scanResults
        var result: ScanResult? = null;
        list.forEach {
            if (it.BSSID == "b2:e6:6f:d7:6c:d7") {
                Log.e("WIFI", it.BSSID)
                result = it
                return@forEach
            }
        }
        if (os!=null){
            os.write("${Calendar.getInstance().timeInMillis},${result?.BSSID},${result?.level} \r\n".toByteArray())
            Log.e("success", "${result?.BSSID}  ${result?.level}")
            os.close();
        }else{
            Log.e("failure", "os为空" )
        }
    }

    /**
     * wifi扫描失败
     */
    private fun scanFailure() {
        val results = wifiManager.scanResults
        Log.e("failure", "failure ")
    }

    /**
     * 开始扫描LTE
     * @param telephonySignalCallBack TelephonyCallback
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun scanLTE(telephonySignalCallBack: TelephonyCallback) {
        telephonyManager.registerTelephonyCallback(
            context.mainExecutor, telephonySignalCallBack
        )
    }

    /**
     * 开始扫描LTE
     * @param phoneStateListener PhoneStateListener
     */
    @Suppress("DEPRECATION")
    fun scanLTE(phoneStateListener: MyPhoneStateListener) {
        phoneStateListener.setManager(telephonyManager)
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    }

    /**
     * 停止扫描LTE
     * @param telephonySignalCallBack TelephonyCallback
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun stopScanLTE(telephonySignalCallBack: TelephonyCallback) {
        telephonyManager.unregisterTelephonyCallback(telephonySignalCallBack)
    }

    /**
     * 停止扫描
     * @param listener PhoneStateListener
     */
    fun stopScanLTE(listener: PhoneStateListener) {
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE)
    }

    /**
     * 关闭流
     */
    fun unregisterWifi() {
        context.unregisterReceiver(wifiScanReceiver)
    }
}
