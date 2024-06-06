package com.netlight.sec.finstergram.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.netlight.sec.finstergram.service.ImageStoreService
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ImageStore {

    private const val IMAGES_DIR = "my_images"


    fun loadImages(context: Context): List<Pair<Bitmap, String>> {
        val folder = File(context.filesDir, IMAGES_DIR)
        if (!folder.exists()) {
            folder.mkdirs() // Create the folder if it doesn't exist
        }
        return folder.listFiles()?.mapNotNull {
            val image = BitmapFactory.decodeFile(it.absolutePath)
            if (image == null){
                null
            } else {
                Pair(image, it.absolutePath)
            }
        } ?: emptyList()
    }

    fun storeImage(context: Context, imageUri: Uri) {
        val intent = Intent(ImageStoreService.INTENT_ACTION_FINSTERGRAM_IMG_SERVICE)
        intent.putExtra(
            ImageStoreService.INTENT_EXTRA_COMMAND,
            ImageStoreService.Command.STORE_IMAGE.name
        )
        intent.data = imageUri
        context.applicationContext.sendBroadcast(intent)
    }

    fun storeImageSync(context: Context, imageUri: Uri) {
        val destinationPath =
            "${context.filesDir.absolutePath}/${IMAGES_DIR}/${generateImageName()}"
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val outputStream = FileOutputStream(File(destinationPath))
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        inputStream.close()
        outputStream.close()
    }

    private fun generateImageName(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }

    fun deleteAllImages(context: Context) {
        val intent = Intent(ImageStoreService.INTENT_ACTION_FINSTERGRAM_IMG_SERVICE)
        intent.putExtra(
            ImageStoreService.INTENT_EXTRA_COMMAND,
            ImageStoreService.Command.DELETE_ALL.name
        )
        context.sendBroadcast(intent)
    }

    fun deleteAllImagesSync(context: Context) {
        val folder = File(context.filesDir, IMAGES_DIR)
        folder.listFiles()?.forEach {
            it.delete()
        }
    }

    fun deleteImage(context: Context, imagePath: String) {
        val intent = Intent(ImageStoreService.INTENT_ACTION_FINSTERGRAM_IMG_SERVICE)
        intent.putExtra(
            ImageStoreService.INTENT_EXTRA_COMMAND,
            ImageStoreService.Command.DELETE_IMAGE.name
        )
        intent.putExtra(ImageStoreService.INTENT_EXTRA_DESTINATION_PATH, imagePath)
        context.sendBroadcast(intent)
    }

    fun deleteImageSync(imagePath: String) {
        val imageFile = File(imagePath)
        imageFile.delete()
    }
}