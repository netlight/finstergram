package com.netlight.sec.finstergram.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.netlight.sec.finstergram.R
import com.netlight.sec.finstergram.data.UserSettings
import com.netlight.sec.finstergram.service.ImageStoreService
import com.netlight.sec.finstergram.ui.FinstergramBaseActivity
import com.netlight.sec.finstergram.ui.list.ImageListActivity


class LoginActivity : FinstergramBaseActivity() {

    private lateinit var viewModel: LoginViewModel

    private val welcomeTitle get() = findViewById<TextView>(R.id.welcomeTitle)

    private val welcomeSubtitle get() = findViewById<TextView>(R.id.welcomeSubtitle)
    private val registerOrLoginButton get() = findViewById<Button>(R.id.buttonRegisterLogin)

    private val usernameField get() = findViewById<EditText>(R.id.username)

    private val passwordField get() = findViewById<EditText>(R.id.password)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val serviceIntent = Intent(this, ImageStoreService::class.java)
        startService(serviceIntent)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        viewModel.init()

        if (viewModel.registeredUser.value != null &&
            !UserSettings.instance.requirePassword
        ) {
            // password check was disabled in settings -> go directly to images
            navigateToImageList()
            return
        }

        viewModel.registeredUser.observe(this) { username ->
            if (username != null) {
                welcomeTitle.text = String.format(getString(R.string.welcome_user), username)
                welcomeSubtitle.text = getString(R.string.please_login)
                registerOrLoginButton.text = "Login"
                usernameField.visibility = View.GONE
            } else {
                welcomeTitle.text = getString(R.string.welcome)
                welcomeSubtitle.text = getString(R.string.welcome_subtitle)
                registerOrLoginButton.text = "Register"
                usernameField.visibility = View.VISIBLE
            }
        }

        registerOrLoginButton.setOnClickListener {
            if (viewModel.registeredUser.value != null) {
                onLoginButtonClick(it)
            } else {
                onRegisterButtonClick(it)
            }
        }
    }

    override fun setBackgroundColor(color: Int) =
        findViewById<ConstraintLayout>(R.id.rootView).setBackgroundColor(color)

    private fun onLoginButtonClick(view: View?) {
        val authenticated = viewModel.authenticateUser(
            passwordField.text.toString()
        )
        if (authenticated) {
            navigateToImageList()
        } else {
            Snackbar.make(view!!, "Wrong credentials!", BaseTransientBottomBar.LENGTH_SHORT)
                .show()
        }
    }

    private fun onRegisterButtonClick(view: View?) {
        val userName = usernameField.text.toString()
        val password = passwordField.text.toString()
        if (userName.isEmpty() || password.isEmpty()) {
            Snackbar.make(
                view!!,
                "Empty Username or Password is not allowed!",
                BaseTransientBottomBar.LENGTH_SHORT
            ).show()
            return
        }
        val registrationSuccessful = viewModel.register(
            userName,
            password
        )

        if (registrationSuccessful) {
            Snackbar.make(
                view!!, "Registration successful", BaseTransientBottomBar.LENGTH_SHORT
            ).show()
            navigateToImageList()
        } else {
            Snackbar.make(view!!, "Registration failed!", BaseTransientBottomBar.LENGTH_SHORT)
                .show()
        }
    }

    private fun navigateToImageList() {
        val intent = Intent(this, ImageListActivity::class.java)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        usernameField.text = null
        passwordField.text = null
    }
}
