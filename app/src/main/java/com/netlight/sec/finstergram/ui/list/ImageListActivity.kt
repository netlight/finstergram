package com.netlight.sec.finstergram.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView.GONE
import android.widget.AdapterView.VISIBLE
import android.widget.ListView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.netlight.sec.finstergram.R
import com.netlight.sec.finstergram.data.ImageStore
import com.netlight.sec.finstergram.data.UserSettings
import com.netlight.sec.finstergram.ui.FinstergramBaseActivity
import com.netlight.sec.finstergram.ui.detail.ImageDetailActivity
import com.netlight.sec.finstergram.ui.import.ImageImportActivity
import com.netlight.sec.finstergram.ui.settings.SettingsActivity
import java.io.File


class ImageListActivity : FinstergramBaseActivity() {

    private lateinit var viewModel: ImageListViewModel

    private val listView: ListView get() = findViewById(R.id.listView)

    private val noImagesMessage: TextView get() = findViewById(R.id.noImages)

    private val addButton: FloatingActionButton get() = findViewById(R.id.fab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)

        viewModel = ViewModelProvider(this)[ImageListViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        addButton.setOnClickListener {
            val intent = Intent(this, ImageImportActivity::class.java)
            startActivity(intent)
        }

        val itemList = ImageStore.loadImages(this).map { ListItem(it.first, it.second) }
        createImageList(itemList)

        invalidateOptionsMenu()
    }

    override fun setBackgroundColor(color: Int) =
        findViewById<CoordinatorLayout>(R.id.coordinatorLayout).setBackgroundColor(color)

    private fun createImageList(itemList: List<ListItem>) {
        listView.adapter = ListAdapter(
            this,
            itemList,
            onDeleteItem = { itemToRemove ->
                viewModel.deleteImage(itemToRemove.fileName)
                createImageList(itemList.filter { it !== itemToRemove })
            },
            onShareItem = { itemToShare ->
                val imageUri =
                    FileProvider.getUriForFile(
                        this,
                        "com.netlight.sec.finstergram.imageprovider",
                        File(itemToShare.fileName)
                    )
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "image/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            },
            onClickItem = { imageClicked ->
                val intent = Intent(this, ImageDetailActivity::class.java)
                intent.putExtra("imagePath", imageClicked.fileName)
                startActivity(intent)
            }
        )

        noImagesMessage.visibility = if (itemList.isNotEmpty()) GONE else VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        if(!UserSettings.instance.requirePassword) {
            menu.findItem(R.id.action_logout).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_account -> {
                viewModel.deleteAccount()
                finish()
                true
            }

            R.id.action_logout -> {
                finish()
                true
            }

            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}


