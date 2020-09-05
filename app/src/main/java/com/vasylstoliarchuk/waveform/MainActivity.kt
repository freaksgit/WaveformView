package com.vasylstoliarchuk.waveform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val values = (0..200).map { Random.nextFloat() }
        val max = values.maxByOrNull { it } ?: 10000f


        waveform.data = values.map { it / max }.toTypedArray()

    }
}