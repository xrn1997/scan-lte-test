package edu.yus.cellInfo

import android.os.Build
import android.telephony.CellSignalStrengthLte
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import edu.yus.cellInfo.MyApplication.Companion.context
import java.util.*

/**
 * 回调类
 * @author xrn1997
 * @date 2022/3/31 19:07.
 */
@RequiresApi(Build.VERSION_CODES.S)
class MyTelephonyCallback(
    textView: TextView
) : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
    private var mTextView = textView
    private val lteUri =
        FileUtils.getFileUri(context, "lte--${Calendar.getInstance().time}.csv", "text/csv")

    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
        val os = lteUri?.let { context.contentResolver.openOutputStream(it, "wa") }
        for (i in signalStrength.cellSignalStrengths) {
            if (i is CellSignalStrengthLte) {
                os?.write("${Calendar.getInstance().timeInMillis},${i.dbm} \r\n".toByteArray())
                Log.e("DBM", i.dbm.toString())
                mTextView.text = i.dbm.toString()
                break
            } else {
                mTextView.text = context.getString(R.string.none_lte)
            }
        }
        os?.close()
    }

}