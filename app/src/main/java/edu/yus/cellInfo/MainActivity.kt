package edu.yus.cellInfo

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.permissionx.guolindev.PermissionX
import edu.yus.cellInfo.databinding.ActivityMainBinding


/**
 * 主Activity
 * @author xrn1997
 * @date 2022/3/30 19:44
 */
class MainActivity : AppCompatActivity() {

    private var telephonySignalCallBack: MyTelephonyCallback? = null
    private var phoneStateListener: MyPhoneStateListener? = null
    private lateinit var viewModel: CollectViewModel

    //ViewBinding
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView: View = binding.root
        setContentView(rootView)
        viewModel = ViewModelProvider(this).get(CollectViewModel::class.java)
        initListener()
        requestPermission()
    }

    private fun requestPermission() {
        PermissionX
            .init(this)
            .permissions(allNeedPermissions())
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "即将重新申请的权限是程序必须依赖的权限(请选择始终)",
                    "我已明白",
                    "取消"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "您需要去应用程序设置当中手动开启权限",
                    "我已明白",
                    "取消"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                    Toast.makeText(this, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
                } else {
                    finish()
                }
            }
    }

    /**
     * 所有需要的权限
     */
    private fun allNeedPermissions(): List<String> {
        val permissions: MutableList<String> = ArrayList()
        permissions.apply {
            add(Manifest.permission.READ_PHONE_STATE)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_WIFI_STATE)
            add(Manifest.permission.CHANGE_WIFI_STATE)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return permissions
    }


    private fun initListener() {

        binding.button.setOnClickListener { //恢复停止录音按钮，并禁用开始录音按钮
            when (binding.button.text) {
                "Start" -> {
                    binding.button.setText(R.string.stop)
                    viewModel.isScanWifi = true
                    viewModel.startScanWifi()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        telephonySignalCallBack = MyTelephonyCallback(binding.textView)
                        viewModel.startScanTLE(telephonySignalCallBack)
                    } else {
                        phoneStateListener = MyPhoneStateListener(binding.textView)
                        viewModel.startScanTLE(phoneStateListener)
                    }
                }
                "Stop" -> {
                    binding.button.setText(R.string.start)
                    viewModel.isScanWifi = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        viewModel.stopScanTLE(telephonySignalCallBack)
                    } else {
                        viewModel.stopScanTLE(phoneStateListener)
                    }

                }
            }
        }
    }
}