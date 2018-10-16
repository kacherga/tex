package com.example.literalnon.autoreequipment.activeEntry

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.literalnon.autoreequipment.EXTRA_PHOTO_TITLE
import com.example.literalnon.autoreequipment.Photo
import com.example.literalnon.autoreequipment.R
import com.example.literalnon.autoreequipment.adapters.AbstractAdapterDelegate
import com.example.literalnon.autoreequipment.allPhotoTypes
import com.example.literalnon.autoreequipment.data.Entry
import com.example.literalnon.autoreequipment.data.RealmPhoto
import com.example.literalnon.autoreequipment.data.WorkType
import io.realm.RealmList
import kotlinx.android.synthetic.main.item_active_entry.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList

/**
 * Created by bloold on 16.12.17.
 */
typealias CheckCallback = HashMap<Int, Entry>

typealias EditCallback = (Entry) -> Unit
typealias RemoveCallback = (Entry, Int) -> Unit

class ActiveEntryDelegate(private val checkCallback: CheckCallback,
                          private val removeCallback: RemoveCallback,
                          private val editCallback: EditCallback
) : AbstractAdapterDelegate<Any, Any, ActiveEntryDelegate.Holder>() {

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
            val context = tvTitle.context

            tvTitle.text = item.name + item.workTypes?.fold("\n") { s: String, type: WorkType ->
                "$s${if (!TextUtils.equals(type.name, EXTRA_PHOTO_TITLE)) {
                    type.name + "\n"
                } else {
                    ""
                }}"
            }

            setChecked(holder, item, holder.adapterPosition, true)

            mainLayout.setOnClickListener {
                if (item.sendedAt != null && !checkCallback.containsKey(holder.adapterPosition)) {
                    AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.no_send_title))
                            .setMessage(context.getString(R.string.no_resend))
                            .setPositiveButton("Да") { dialog, which ->
                                val entry = Entry()
                                entry.phone = item.phone
                                entry.name = item.name
                                entry.sendedAt = null
                                entry.sendType = 0
                                entry.workTypes = RealmList()
                                item.workTypes?.forEach {
                                    val workType = WorkType()
                                    workType.description = it.description
                                    workType.name = it.name
                                    workType.sendedAt = null
                                    workType.photos = RealmList()
                                    it.photos?.forEach {
                                        val photo = RealmPhoto()
                                        photo.sendedAt = null
                                        photo.id = it.id
                                        photo.name = it.name
                                        photo.photo = it.photo
                                        photo.type = it.type
                                        workType.photos?.add(photo)
                                    }
                                    entry.workTypes?.add(workType)
                                }
                                setChecked(holder, entry, holder.adapterPosition)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Нет") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                } else {
                    setChecked(holder, item, holder.adapterPosition)
                }
            }

            btnEdit.setOnClickListener {
                editCallback(item)
            }

            btnDelete.setOnClickListener {
                AlertDialog.Builder(context)
                        .setTitle("Удаление записи")
                        .setMessage("Вы действительно хотите удалить запись?")
                        .setPositiveButton("Да") { dialog, which ->
                            removeCallback(item, holder.adapterPosition)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Нет") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
            }

            tvSendedAt.text = if (item.sendedAt != null && item.sendType == 1) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = item.sendedAt!!
                SimpleDateFormat("dd:MM hh:mm").format(calendar.time)
            } else if (item.sendType == 2) {
                context?.getString(R.string.no_sended_file_half)
            } else {
                context?.getString(R.string.no_sended_file)
            }
        }
    }

    private fun setChecked(holder: Holder, item: Entry, position: Int, isContains: Boolean = false) {
        if (checkCallback.containsKey(position) == isContains) {
            checkCallback[position] = item
            holder.ivCheck.visibility = View.VISIBLE
            holder.checkView.visibility = View.VISIBLE
        } else {
            checkCallback.remove(position)
            holder.ivCheck.visibility = View.INVISIBLE
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
        val tvSendedAt = itemView.tvSendedAt
    }
}