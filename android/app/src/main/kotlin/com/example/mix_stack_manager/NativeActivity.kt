package com.example.mix_stack_manager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_native.*

class NativeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native)

        buttonOpenFlutter.setOnClickListener {
            var intent = Intent(this, HostActivity::class.java)
            intent.putExtra("route", FlutterMapper.FLUTTER_DETAIL_WIDGET)
            startActivity(intent)
        }
    }
}