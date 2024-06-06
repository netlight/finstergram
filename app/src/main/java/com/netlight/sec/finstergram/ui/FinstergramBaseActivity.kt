package com.netlight.sec.finstergram.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.netlight.sec.finstergram.data.UserSettings

abstract class FinstergramBaseActivity : AppCompatActivity() {

    val dark = Color.parseColor("#68696b")

    val light = Color.parseColor("#ffffff")

    abstract fun setBackgroundColor(color: Int)

    override fun onResume() {
        super.onResume()

        val backGroundColor = if (UserSettings.instance.darkMode) {
            dark
        } else {
            light
        }
        setBackgroundColor(backGroundColor)
    }
}