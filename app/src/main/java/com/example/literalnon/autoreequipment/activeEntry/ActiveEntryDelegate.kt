package com.example.literalnon.autoreequipment.activeEntry

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.support.v7.app.AlertDialog
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
import com.example.literalnon.autoreequipment.data.WorkType
import kotlinx.android.synthetic.main.item_active_entry.view.*
import java.io.File
import javax.security.auth.callback.Callback

/**
 * Created by bloold on 16.12.17.
 */
typealias CheckCallback = HashMap<Int, Entry>
typealias EditCallback = (Entry) -> Unit
typealias RemoveCallback = (Entry, Int) -> Unit

class ActiveEntryDelegate(private val checkCallback: CheckCallback,
                          private val editCallback: EditCallback,
                          private val removeCallback: RemoveCallback) : AbstractAdapterDelegate<Any, Any, ActiveEntryDelegate.Holder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return item is Entry
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_active_entry, parent, false)
        //view.layoutParams.height = (Resources.getSystem().displayMetrics.widthPixels - SpaceItemDecoration.distance * 4) / 3
        //view.layoutParams.width = (Resources.getSystem().displayMetrics.widthPixels - SpaceItemDecoration.distance * 4) / 3
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, item: Any) {
        item as Entry

        with(holder) {
            tvTitle.text = item.name + item.workTypes?.fold("\n") { s: String, type: WorkType ->
                "$s${type.name}\n"
            }

            setChecked(holder, item, position, true)

            mainLayout.setOnClickListener {
                setChecked(holder, item, position)
            }

            btnEdit.setOnClickListener {
                editCallback(item)
            }

            btnDelete.setOnClickListener {
                AlertDialog.Builder(tvTitle.context)
                        .setTitle("Удаление записи")
                        .setMessage("Вы действительно хотите удалить запись?")
                        .setPositiveButton("Да") { dialog, which ->
                            removeCallback(item, position)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Нет") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
            }

        }
    }

    fun setChecked(holder: Holder, item: Entry, position: Int, isContains: Boolean = false) {
        if (checkCallback.containsKey(position) == isContains) {
            checkCallback[position] = item
            holder.ivCheck.visibility = View.VISIBLE
            holder.checkView.visibility = View.VISIBLE
        } else {
            checkCallback.remove(position)
            holder.ivCheck.visibility = View.GONE
            holder.checkView.visibility = View.GONE
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.tvTitle
        val mainLayout = itemView.mainLayout
        val checkView = itemView.checkView
        val ivCheck = itemView.ivCheck
        val btnEdit = itemView.btnEdit
        val btnDelete = itemView.btnDelete
    }
}