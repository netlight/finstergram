package com.netlight.sec.finstergram.ui.shared

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.netlight.sec.finstergram.R
import com.netlight.sec.finstergram.data.ImageStore
import com.netlight.sec.finstergram.ui.FinstergramBaseActivity
import com.netlight.sec.finstergram.ui.login.LoginActivity
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
class SharedImageActivity : FinstergramBaseActivity() {

    private val rootView get() = findViewById<LinearLayout>(R.id.rootView)

    private val continueButton get() = findViewById<Button>(R.id.continueButton)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_image)
    }

    override fun onResume() {
        super.onResume()

        continueButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
        val imageMetadataOptions = parseMetadataOptions(intent)
        if (imageUri != null) {
            importImage(imageUri, imageMetadataOptions)
            Snackbar.make(rootView, "Image imported!", BaseTransientBottomBar.LENGTH_SHORT)
                .show()
            setResult(RESULT_OK, intent)
        } else {
            setResult(RESULT_CANCELED, intent)
            finish()
        }
    }

    private fun parseMetadataOptions(intent: Intent): MetadataOptions? {
        val optionType = intent.getStringExtra(INTENT_EXTRA_IMG_METADATA_OPTION_TYPE)
        val option = intent.getStringExtra(INTENT_EXTRA_IMG_METADATA_OPTION)
        if (optionType != null && option != null) {
            val optionClass = Class.forName(optionType).kotlin
            val serializer = optionClass.serializer()
            return Json.decodeFromString(serializer, option) as? MetadataOptions
        }
        return null
    }

    private fun importImage(imageUri: Uri, metadataOptions: MetadataOptions?) {
        // TODO: applying metadata options is not implemented yet (moved to Q4 next year)
        ImageStore.storeImage(this, imageUri)
    }

    /**
     * We want to give other apps some options regarding what should happen to image metadata when
     * their image is imported into Finstergram.
     */
    sealed class MetadataOptions {

        class AddMetadata(val data: String) : MetadataOptions()

        object KeepMetadata : MetadataOptions()

        object StripMetadata : MetadataOptions()
    }

    override fun setBackgroundColor(color: Int) = rootView.setBackgroundColor(color)

    companion object {
        const val INTENT_EXTRA_IMG_METADATA_OPTION_TYPE = "INTENT_EXTRA_IMG_METADATA_OPTION_TYPE"
        const val INTENT_EXTRA_IMG_METADATA_OPTION = "INTENT_EXTRA_IMG_METADATA_OPTION"
    }
}