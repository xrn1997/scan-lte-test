package edu.yus.cellInfo

import android.Manifest
import android.content.pm.PackageManager
import android.telephony.CellInfoLte
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import edu.yus.cellInfo.MyApplication.Companion.context
import java.util.*

/**
 *
 * @author xrn1997
 * @date 2022/3/31 19:46.
 */
@Suppress("DEPRECATION")
class MyPhoneStateListener(
    textView: TextView
) : PhoneStateListener() {
    private val mTextView = textView
    private var mManager: TelephonyManager? = null
    private val lteUri =
        FileUtils.getFileUri(context, "lte--${Calendar.getInstance().time}.csv", "text/csv")

    override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
        val cellInfoList = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            null
        } else {
            mManager?.allCellInfo
        }
        if (null != cellInfoList) {
            val os = lteUri?.let { context.contentResolver.openOutputStream(it, "wa") }
            for (cellInfo in cellInfoList) {
                if (cellInfo.isRegistered && cellInfo is CellInfoLte) {
                    os?.write("${Calendar.getInstance().timeInMillis},${cellInfo.cellSignalStrength.dbm} \r\n".toByteArray())
                    Log.e("DBM", cellInfo.cellSignalStrength.dbm.toString())
                    mTextView.text = cellInfo.cellSignalStrength.dbm.toString()
                    break
                } else {
                    mTextView.text = context.getString(R.string.none_lte)
                }
            }
            os?.close()
        }
        super.onSignalStrengthsChanged(signalStrength)
    }

    fun setManager(manager: TelephonyManager) {
        mManager = manager
    }
}