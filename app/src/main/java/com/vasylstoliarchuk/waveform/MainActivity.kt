package com.vasylstoliarchuk.waveform

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val values = (0..200).map { Random.nextFloat() }
        val max = values.maxOf { it }
        waveform.data = values.map { it / max }.toTypedArray()
        var aProgress = 0f

        waveform.waveformChangeListener = object : WaveformChangeListener {
            override fun onProgressChanged(waveformView: WaveformView, progress: Float, fromUser: Boolean) {
                if (fromUser) aProgress = progress
                Log.d(TAG, "onProgressChanged(progress=$progress, fromUser=$fromUser)")
            }

            override fun onStartTrackingTouch(waveformView: WaveformView) {
                Log.d(TAG, "onStartTrackingTouch()")
            }

            override fun onStopTrackingTouch(waveformView: WaveformView) {
                Log.d(TAG, "onStopTrackingTouch()")
                Toast.makeText(this@MainActivity, "Progress = $aProgress", Toast.LENGTH_SHORT).show()
            }
        }
    }
}