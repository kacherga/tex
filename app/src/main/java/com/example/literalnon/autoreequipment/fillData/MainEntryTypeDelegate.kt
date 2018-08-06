package com.example.literalnon.autoreequipment.fillData

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.literalnon.autoreequipment.EntryType
import com.example.literalnon.autoreequipment.MainEntryType
import com.example.literalnon.autoreequipment.Photo
import com.example.literalnon.autoreequipment.R
import com.example.literalnon.autoreequipment.adapters.AbstractAdapterDelegate
import kotlinx.android.synthetic.main.item_entry_type.view.*
import java.io.File
import javax.security.auth.callback.Callback

/**
 * Created by bloold on 16.12.17.
 */
class MainEntryTypeDelegate : AbstractAdapterDelegate<Any, Any, MainEntryTypeDelegate.Holder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is EntryType
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_entry_type, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, item: Any) {
        item as EntryType

        with(holder) {
            tvTitle.text = item.title
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mainLayout = itemView.mainLayout
        val tvTitle = itemView.tvTitle
    }
}