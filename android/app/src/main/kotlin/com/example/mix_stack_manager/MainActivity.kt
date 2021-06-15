package com.example.mix_stack_manager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        Utils.init(application)

        buttonInitFlutter.setOnClickListener {


            FlutterManager.instance.initFlutterEngine(this) {
                when (it) {
                    0 -> {
                        ToastUtils.showShort("FlutterEngine已经初始化完毕了！")
                    }
                    1 -> {
                        showLoading()
                    }
                    2 -> {
                        hiddenLoading()
                        ToastUtils.showShort("初始化完毕！")
                    }
                }
            }
        }

        startFlutter.setOnClickListener {
            if (FlutterManager.instance.isInitFinish()) {
                var intent = Intent(this, HostActivity::class.java)
                intent.putExtra("route", FlutterMapper.FLUTTER_HOME_WIDGET)
                startActivity(intent)
            } else {
                ToastUtils.showShort("请先初始化FlutterEngine")
            }
        }

        releaseFlutter.setOnClickListener {
            FlutterManager.instance.release()
        }
    }

    private inline fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = AlertDialog.Builder(this).setMessage("FlutterEngine正在初始化，请稍候~").show()
        } else {
            loadingDialog?.show()
        }
    }

    private inline fun hiddenLoading() {
        loadingDialog?.dismiss()
    }

}
