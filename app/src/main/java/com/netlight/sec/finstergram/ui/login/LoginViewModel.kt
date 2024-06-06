package com.netlight.sec.finstergram.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netlight.sec.finstergram.data.DatabaseHelper

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext get() = getApplication<Application>()

    private val dbHelper = DatabaseHelper(appContext)

    private val _registeredUser = MutableLiveData<String?>(null)
    val registeredUser: LiveData<String?> get() = _registeredUser

    fun init() {
        _registeredUser.value = dbHelper.getUsernameIfRegistered()
    }

    fun register(username: String, password: String): Boolean {
        val successful = dbHelper.registerUser(
            username,
            password
        )
        if(successful) _registeredUser.value = username
        return successful
    }

    fun authenticateUser(password: String) =
        dbHelper.authenticateUser(_registeredUser.value!!, password)
}