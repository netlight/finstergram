package com.netlight.sec.finstergram.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.netlight.sec.finstergram.R


class ListAdapter(
    private val context: Context,
    private val itemList: List<ListItem>,
    private val onDeleteItem: (ListItem) -> Unit,
    private val onShareItem: (ListItem) -> Unit,
    private val onClickItem: (ListItem) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val resultingView = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.list_item, parent, false)
        } else convertView
        val item = itemList[position]
        val itemImage: ImageView = resultingView.findViewById(R.id.list_item_image)
        itemImage.setImageBitmap(item.image)
        val deleteButton: ImageButton = resultingView.findViewById(R.id.list_item_delete_btn)
        deleteButton.setOnClickListener { onDeleteItem(item) }
        val shareButton: ImageButton = resultingView.findViewById(R.id.list_item_share_btn)
        shareButton.setOnClickListener { onShareItem(item) }
        val itemRoot: LinearLayout = resultingView.findViewById(R.id.list_item_root)
        itemRoot.setOnClickListener { onClickItem(item) }
        return resultingView
    }
}
