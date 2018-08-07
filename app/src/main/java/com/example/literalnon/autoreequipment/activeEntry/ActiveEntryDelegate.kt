package com.example.literalnon.autoreequipment.activeEntry

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.literalnon.autoreequipment.Photo
import com.example.literalnon.autoreequipment.R
import com.example.literalnon.autoreequipment.adapters.AbstractAdapterDelegate
import com.example.literalnon.autoreequipment.data.Entry
import kotlinx.android.synthetic.main.item_active_entry.view.*
import kotlinx.android.synthetic.main.item_create_photo.view.*
import java.io.File
import javax.security.auth.callback.Callback

/**
 * Created by bloold on 16.12.17.
 */
class ActiveEntryDelegate : AbstractAdapterDelegate<Any, Any, ActiveEntryDelegate.Holder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is Entry
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_active_entry, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, item: Any) {
        item as Entry

        with(holder) {
            chbEntry.text = item.name
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chbEntry = itemView.chbEntry
    }
}