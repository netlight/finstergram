package com.netlight.sec.finstergram.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.netlight.sec.finstergram.data.DatabaseHelper
import com.netlight.sec.finstergram.data.ImageStore
import com.netlight.sec.finstergram.data.UserSettings

class ImageListViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext get() = getApplication<Application>()

    private val dbHelper = DatabaseHelper(appContext)

    fun deleteImage(imagePath: String) {
        ImageStore.deleteImage(appContext, imagePath)
    }

    fun deleteAccount() {
        // delete user data
        dbHelper.deleteUsers()

        // reset settings
        UserSettings.reset(appContext)

        // delete all image files
        ImageStore.deleteAllImages(appContext)
    }
}