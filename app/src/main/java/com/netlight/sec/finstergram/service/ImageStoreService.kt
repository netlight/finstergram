package com.netlight.sec.finstergram.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.netlight.sec.finstergram.data.ImageStore

/**
 * The [ImageStoreService] takes care of performing writing operations within the image directory
 * - on a background thread
 * - in the background (i.e. the operation will resume even when the user closes the app)
 */
class ImageStoreService : Service() {

    enum class Command {
        DELETE_IMAGE, DELETE_ALL, STORE_IMAGE
    }

    private val commandReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val command = intent?.getStringExtra(INTENT_EXTRA_COMMAND)
            Log.i("SERVICE", "Received Broadcast with command $command")
            when (command) {
                Command.DELETE_IMAGE.name -> performAsync { handleDeleteImage(intent) }
                Command.DELETE_ALL.name -> performAsync { handleDeleteAllImages() }
                Command.STORE_IMAGE.name -> performAsync { handleStoreImage(intent) }
                else -> {
                    /* do nothing */
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null // not implemented

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter(INTENT_ACTION_FINSTERGRAM_IMG_SERVICE)
        val filterWithData = IntentFilter(INTENT_ACTION_FINSTERGRAM_IMG_SERVICE, "*/*")
        registerReceiver(commandReceiver, filter)
        registerReceiver(commandReceiver, filterWithData)
        Log.i("SERVICE", "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(commandReceiver)
    }

    private fun handleDeleteImage(intent: Intent?) {
        val destinationPath = intent?.getStringExtra(INTENT_EXTRA_DESTINATION_PATH)
        destinationPath?.let { ImageStore.deleteImageSync(destinationPath) }
    }

    private fun handleDeleteAllImages() {
        ImageStore.deleteAllImagesSync(this)
    }

    private fun handleStoreImage(intent: Intent?) {
        intent?.data?.let {
            ImageStore.storeImageSync(this, it)
        }
    }

    private fun performAsync(block: () -> Unit) {
        Thread({ block.invoke() }, "ImageStoreService background thread").start()
    }

    companion object {
        const val INTENT_ACTION_FINSTERGRAM_IMG_SERVICE =
            "INTENT_ACTION_FINSTERGRAM_IMG_SERVICE"
        const val INTENT_EXTRA_COMMAND = "INTENT_EXTRA_COMMAND"
        const val INTENT_EXTRA_DESTINATION_PATH = "INTENT_EXTRA_DESTINATION_PATH"
    }
}