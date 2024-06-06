package com.netlight.sec.finstergram.ui.import

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.netlight.sec.finstergram.R
import com.netlight.sec.finstergram.data.ImageStore
import com.netlight.sec.finstergram.ui.FinstergramBaseActivity


class ImageImportActivity : FinstergramBaseActivity() {

    private val rootView: LinearLayout get() = findViewById(R.id.rootView)

    private val importButton: Button get() = findViewById(R.id.importButton)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_import)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        importButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { imageUri ->
                ImageStore.storeImage(this, imageUri)
                Snackbar.make(rootView, "Image imported!", BaseTransientBottomBar.LENGTH_SHORT)
                    .show()
            }
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

    override fun setBackgroundColor(color: Int) = rootView.setBackgroundColor(color)

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1337
    }
}