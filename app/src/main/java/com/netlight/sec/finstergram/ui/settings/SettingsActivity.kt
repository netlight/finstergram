package com.netlight.sec.finstergram.ui.settings

import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import com.netlight.sec.finstergram.R
import com.netlight.sec.finstergram.data.UserSettings
import com.netlight.sec.finstergram.ui.FinstergramBaseActivity

class SettingsActivity : FinstergramBaseActivity() {

    private val darkModeSwitch: SwitchCompat get() = findViewById(R.id.darkModeSwitch)

    private val passwordRequiredSwitch: SwitchCompat get() = findViewById(R.id.passwordRequiredSwitch)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val settings = UserSettings.instance
        darkModeSwitch.isChecked = settings.darkMode
        passwordRequiredSwitch.isChecked = settings.requirePassword

        darkModeSwitch.setOnCheckedChangeListener { _, checked ->
            UserSettings.store(this, darkMode = checked)
            setBackgroundColor(
                if (checked) dark else light
            )
        }
        passwordRequiredSwitch.setOnCheckedChangeListener { _, checked ->
            UserSettings.store(this, requirePassword = checked)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun setBackgroundColor(color: Int) =
        findViewById<LinearLayout>(R.id.rootView).setBackgroundColor(color)
}