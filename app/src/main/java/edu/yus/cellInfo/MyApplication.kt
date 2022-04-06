package edu.yus.cellInfo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 *  Application
 * @author xrn1997
 * @date 2022/3/30 19:46
 */
class MyApplication :Application(){
    companion object{
    @SuppressLint("StaticFieldLeak")
    @JvmStatic
    lateinit var context: Context
}
    override fun onCreate() {
        super.onCreate()
        context =applicationContext
    }
}