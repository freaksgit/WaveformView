package com.vasylstoliarchuk.waveform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        waveform.data = floatArrayOf(0.7f, 0.1f, 0.2f, 1f, 0.9f, 0.1f, 0.7f, 0.1f, 0.2f, 1f, 0.9f, 0.1f, 0.7f, 0.1f, 0.2f, 1f, 0.9f, 0.1f, 0.7f, 0.1f, 0.2f, 1f, 0.9f, 0.1f,0.7f, 0.1f, 0.2f, 1f, 0.9f, 0.1f, 0.7f, 0.1f, 0.2f, 1f, 0.9f, 0.1f, 0.7f, 0.1f, 0.2f, 1f, 0.9f, 0.1f, 0.7f, 0.1f, 0.2f, 1f, 0.9f, 0.1f)

    }
}