package com.example.literalnon.autoreequipment.activeEntry

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.*
import android.util.Log
import com.example.literalnon.autoreequipment.*
import com.example.literalnon.autoreequipment.adapters.DelegationAdapter
import com.example.literalnon.autoreequipment.data.*
import com.example.literalnon.autoreequipment.utils.UpdateService
import com.example.literalnon.autoreequipment.utils.UpdateService.Companion.EXTRA_ENTRY_OBJECT
import com.example.literalnon.autoreequipment.utils.UpdateService.Companion.EXTRA_IS_RESTART
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_active_entry.*
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.ActiveEntryPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IActiveEntryPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IActiveEntryView
import kotlin.collections.ArrayList


class ActiveEntryFragment : Fragment(), IActiveEntryView {

    companion object {
        const val EXTRA_IS_ACTIVE = "is_active"

        fun newInstance(isActive: Boolean) = ActiveEntryFragment().apply {
            arguments = Bundle().apply {
                putBoolean(EXTRA_IS_ACTIVE, isActive)
            }
        }
    }

    override var presenter: IActiveEntryPresenter = ActiveEntryPresenter()
    private val adapter = DelegationAdapter<Any>()

    private var realm: Realm? = null
    private val checkedEntries = CheckCallback()
    val subscriptions = CompositeDisposable()
    private var isActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments?.containsKey(EXTRA_IS_ACTIVE) == true) {
            isActive = arguments?.getBoolean(EXTRA_IS_ACTIVE) ?: true
        }
    }

    private val callback = RealmChangeListener<RealmResults<Entry>> {
        adapter.replaceAll(it.filter {
            if (!isActive) {
                it.sendType == 1
            } else {
                true
            } && (it.sendedAt == null) == isActive
        }.map { it.toObject() })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_active_entry, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        realm = Realm.getDefaultInstance()

        adapter.manager?.addDelegate(ActiveEntryDelegate(checkedEntries, { entry, position ->

            realm?.beginTransaction()

            realm?.where(Entry::class.java)?.`in`("name", arrayOf(entry.name
                    ?: ""))?.`in`("phone", arrayOf(entry.phone
                    ?: ""))?.findAll()?.deleteAllFromRealm()

            realm?.commitTransaction()

            realm?.executeTransaction({ bgRealm ->

            })

            adapter.remove(position)
        }, { entry ->
            /*val name = entry.name ?: ""
            val phone = entry.phone ?: ""
            var sendExtras: Extras? = null

            val choiceTypes = ArrayList<EntryType>().apply {
                addAll(allEntryType.filter {
                    entry.workTypes?.find { workType -> TextUtils.equals(workType.name, it.title) } != null
                })

                *//*photos.forEach {
                    it.photos = ArrayList()
                }*//*

                entry.workTypes?.forEach {
                    Log.e("workTypes", it?.photos?.count()?.toString())
                    it?.photos?.forEach {
                        //if (photos[it.id!!].photos == null) {
                        Log.e("workTypes", "id : ${it.id}")

                        //}
                        *//*if (photos[it.id!!].photos?.contains(it.photo) == false) {
                            photos[it.id!!].photos?.add(it.photo ?: "")
                        }*//*
                    }
                }

                val extras = entry.workTypes?.find { TextUtils.equals(it.name, EXTRA_PHOTO_TITLE) }
                sendExtras = if (extras != null) {
                    Extras(
                            extras.description ?: "",
                            extras.photos?.map { it.photo ?: "" }
                    )
                } else {
                    null
                }
            }*/

            //нужно добавить фотографии для передачи
            presenter.openEdit(entry)
        }))

        rvActiveEntry.layoutManager = LinearLayoutManager(context)
        rvActiveEntry.addItemDecoration(SpaceItemDecoration(context))

        rvActiveEntry.adapter = adapter

        //realm?.beginTransaction()

        //val entries =
        realm?.where(Entry::class.java)?.findAllAsync()?.addChangeListener(callback)


        /* var client: Client? = Client(entries?.first()?.name)
         val newEntries = ArrayList<Any>()
         newEntries.add(client!!)

         entries?.forEach {
             if (client?.name == it.name) {
                 client?.entries?.add(it)
             } else {
                 client = Client(it.name)
                 client?.entries = arrayListOf()
                 client?.entries?.add(it)
                 newEntries.add(client!!)
             }

             newEntries.add(it)
         }*/

        //val listEntry = ArrayList<EntryObject>()

        btnNext.setOnClickListener {
            //Log.e("makeDirectory", "onClick")

            if (!UpdateService.isDownloading) {
                val listEntry = ArrayList<EntryObject>()

                if (adapter.itemCount > 0 && checkedEntries.size > 0 && LoginController.user?.phone?.isNotEmpty() == true) {
                    AlertDialog.Builder(context)
                            .setTitle("Отправка")
                            .setMessage("Данные будут отправлены на сервер, статус отправки можно посмотреть в верхнем меню.")
                            .setNeutralButton("ok") { dialog, k ->
                                dialog.cancel()
                            }
                            .show()

                    checkedEntries.forEach { (key, value) ->
                        //Log.e("makeDirectory", value.name.toString())

                        listEntry.add(
                                EntryObject(
                                        value.name,
                                        value.phone,
                                        value.workTypes?.map {
                                            //Log.e("makeDirectory", it.name.toString())
                                            WorkTypeObject(
                                                    it.name,
                                                    it.description,
                                                    it.sendedAt,
                                                    it.photosId
                                            )
                                        },
                                        0,
                                        value.sendedAt,
                                        value.photos?.map {
                                            //Log.e("makeDirectory", it.name.toString())
                                            PhotoObject(
                                                    it.photoId,
                                                    it.name,
                                                    it.photo,
                                                    it.type,
                                                    it.sendedAt
                                            )
                                        }
                                )
                        )

                    }

                    //showLoading()

                    context?.startService(getServiceIntent(context!!, Bundle().apply {
                        putString(EXTRA_ENTRY_OBJECT, Gson().toJson(listEntry.map {
                            EntryObject(it.name, it.phone)
                        }))
                        putBoolean(EXTRA_IS_RESTART, !isActive)
                        //putString(EXTRA_PHOTOS, Gson().toJson())
                    }))

                } else if (checkedEntries.size == 0) {
                    Toast.makeText(context, getString(R.string.send_file_no_checked), Toast.LENGTH_SHORT).show()
                } else if (!(LoginController.user?.phone?.isNotEmpty() == true)) {
                    Toast.makeText(context, getString(R.string.send_file_no_phone), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, getString(R.string.send_file_error), Toast.LENGTH_SHORT).show()
                }
            } else {
                //Toast.makeText(context, getString(R.string.send_file_is_downloading), Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(context)
                        .setTitle("Отправка")
                        .setMessage(R.string.send_file_cancel)
                        .setPositiveButton("ДА") { dialog, k ->
                            UpdateService.stop()
                            dialog.cancel()
                        }
                        .setNegativeButton("НЕТ") { dialog, k ->
                            dialog.cancel()
                        }
                        .show()
            }
        }
    }

    private fun getServiceIntent(context: Context, extra: Bundle?): Intent {
        val intent = Intent(context, UpdateService::class.java)

        if (extra != null) {
            intent.putExtras(extra)
        }

        return intent
    }

    /*private fun addFiles(entries: List<EntryObject>?) {

        //try {
        val ftpClient = FTPClient()
        ftpClient.controlEncoding = "UTF-8"

        ftpClient.connect(getString(R.string.ftp_client))

        //Log.e("makeDirectory", "login")

        if (ftpClient.login(getString(R.string.ftp_login), getString(R.string.ftp_password))) {
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

            val companyName = "${LoginController.user?.phone
                    ?: "Без города"}/${LoginController.user?.name?.lines()?.fold("") { acc, s ->
                "$acc $s"
            }?.trim() ?: "Тестовая компания"}"

            //Log.e("makeDirectory", companyName + " : " + ftpClient.controlEncoding)

            //ftpClient.makeDirectory((LoginController.user?.town ?: "Без города"))
            //ftpClient.makeDirectory(companyName)

            entries?.forEach {
                val path = "$companyName/${it.name?.lines()?.fold("") { acc, s ->
                    "$acc $s"
                }?.trim()}"
                //ftpClient.makeDirectory(path)
                //Log.e("makeDirectory", "${path}")
                it.workTypes?.forEach {
                    val workTypePath = path + "/" + it.name
                    //ftpClient.makeDirectory(workTypePath)
                    //Log.e("makeDirectory", "${workTypePath}")
                    it.photos?.forEach {
                        //ftpClient.makeDirectory(workTypePath + "/" + it.type)
                        //Log.e("appendFile", "${workTypePath + "/" + it.type + "/" + it.name} ${it.photo}")
                        if (it.photo != null)
                            try {
                                ftpClient.makeDirectory((LoginController.user?.phone
                                        ?: "Без города"))
                                ftpClient.makeDirectory(companyName)
                                ftpClient.makeDirectory(path)
                                ftpClient.makeDirectory(workTypePath)
                                ftpClient.makeDirectory(workTypePath + "/" + it.type)
                                ftpClient.appendFile(workTypePath + "/" + it.type + "/" + (it.name
                                        ?: "photo") + ".jpg", File(it.photo).inputStream())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                    }
                }
            }

            ftpClient.logout()
            ftpClient.disconnect()
        }
        *//*} catch (e: Exception) {
            e.printStackTrace()
        }*//*
    }*/

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }

    var progressDialog: ProgressDialog? = null

    override fun showLoading() {
        progressDialog = ProgressDialog.show(context, "Загрузка...", "Пожалуйста подождите!")
        //progressBar?.visibility = View.VISIBLE
    }

    override fun dismissLoading() {
        progressDialog?.dismiss()
        //progressBar?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        subscriptions.clear()
    }
}

