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
import com.example.literalnon.autoreequipment.R
import com.example.literalnon.autoreequipment.adapters.AbstractAdapterDelegate
import kotlinx.android.synthetic.main.item_photo_request.view.*
import java.io.File
import javax.security.auth.callback.Callback

/**
 * Created by bloold on 16.12.17.
 */
class PhotoDelegate(private val callback: (File) -> Unit): AbstractAdapterDelegate<Any, Any, PhotoDelegate.Holder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is File
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo_request, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, item: Any) {
        item as File
        Glide.with(holder.ivClose.context)
                .load(item)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivPhoto)

        holder.ivClose.setOnClickListener { callback(item) }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto = itemView.ivPhoto
        val ivClose = itemView.ivClose
    }
}