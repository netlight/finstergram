package com.netlight.sec.finstergram.ui.detail

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import com.netlight.sec.finstergram.R
import com.netlight.sec.finstergram.ui.FinstergramBaseActivity


class ImageDetailActivity : FinstergramBaseActivity() {

    private val imageView: ImageView get() = findViewById(R.id.image)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val imagePath = intent.getStringExtra("imagePath")
        val image = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(image)
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
