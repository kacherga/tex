package services.mobiledev.ru.cheap.ui.main.comments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.text.style.UnderlineSpan
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.betcityru.dyadichko_da.betcityru.ui.createService
import com.example.literalnon.autoreequipment.*
import com.example.literalnon.autoreequipment.activeEntry.ActiveEntryDelegate
import com.example.literalnon.autoreequipment.activeEntry.CheckCallback
import com.example.literalnon.autoreequipment.activeEntry.SpaceItemDecoration
import com.example.literalnon.autoreequipment.adapters.DelegationAdapter
import com.example.literalnon.autoreequipment.data.*
import com.example.literalnon.autoreequipment.fillData.MainEntryTypeDelegate
import com.example.literalnon.autoreequipment.network.NotificateService
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_active_entry.*
import kotlinx.android.synthetic.main.item_active_entry.*
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.ActiveEntryPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IActiveEntryPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IActiveEntryView
import java.io.File
import java.io.FileInputStream
import java.lang.RuntimeException
import java.util.*


class ActiveEntryFragment : Fragment(), IActiveEntryView {

    companion object {
        fun newInstance() = ActiveEntryFragment()
    }

    override var presenter: IActiveEntryPresenter = ActiveEntryPresenter()
    private val adapter = DelegationAdapter<Any>()

    private var realm: Realm? = null
    private val checkedEntries = CheckCallback()
    val subscriptions = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_active_entry, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        adapter.manager?.addDelegate(ActiveEntryDelegate(checkedEntries, { entry ->
            EnterNameFragment.name = entry.name ?: ""

            AddEntryFragment.choiceTypes = ArrayList<EntryType>().apply {
                addAll(allEntryType.filter {
                    entry.workTypes?.find { workType -> TextUtils.equals(workType.name, it.title) } != null
                })

                entry.workTypes?.forEach {
                    it?.photos?.forEach {
                        photos[it.id!!].photo = it.photo
                    }
                }
                val extras = entry.workTypes?.find { TextUtils.equals(it.name, allPhotoTypes[4].title) }
                if (extras != null) {
                    AddEntryFragment.extras = Extras(
                            extras.photos?.firstOrNull()?.type ?: "",
                            extras.photos?.map { it.photo ?: "" }
                    )
                }
            }
            presenter.openEdit()
        }, { entry, position ->

            realm?.beginTransaction()

            realm?.where(Entry::class.java)?.`in`("name", arrayOf(entry.name
                    ?: ""))?.findAll()?.deleteAllFromRealm()

            realm?.commitTransaction()

            realm?.executeTransaction({ bgRealm ->

            })

            adapter.remove(position)
        }))

        rvActiveEntry.layoutManager = LinearLayoutManager(context)
        rvActiveEntry.addItemDecoration(SpaceItemDecoration(context))

        rvActiveEntry.adapter = adapter

        realm = Realm.getDefaultInstance()
        //realm?.beginTransaction()

        var entries = realm?.where(Entry::class.java)?.findAll()
        adapter.addAll(entries?.toList())

        val listEntry = ArrayList<EntryObject>()

        btnNext.setOnClickListener {
            //Log.e("makeDirectory", "onClick")

            if (adapter.itemCount > 0 && checkedEntries.size > 0) {
                checkedEntries.forEach { (key, value) ->
                    //Log.e("makeDirectory", value.name.toString())

                    listEntry.add(
                            EntryObject(
                                    value.name,
                                    value.workTypes?.map {
                                        //Log.e("makeDirectory", it.name.toString())
                                        WorkTypeObject(
                                                it.name,
                                                it.photos?.map {
                                                    //Log.e("makeDirectory", it.name.toString())
                                                    PhotoObject(
                                                            it.name,
                                                            it.photo,
                                                            it.type
                                                    )
                                                }
                                        )
                                    }
                            )
                    )
                }

                showLoading()

                Observable.create<Unit> {
                    ////Log.e("makeDirectory", "realm = Realm.getDefaultInstance() size : ${listEntry.size} : ${checkedEntries.size}")
                    it.onNext(addFiles(listEntry))

                    it.onComplete()
                }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate {
                            dismissLoading()
                            listEntry.clear()
                        }
                        .subscribe({
                            /*realm?.beginTransaction()

                            realm?.where(Entry::class.java)?.`in`("name", listEntry.map {
                                it.name ?: ""
                            }.toTypedArray())?.findAll()?.deleteAllFromRealm()

                            checkedEntries.clear()

                            realm?.commitTransaction()

                            realm?.executeTransaction({ bgRealm ->

                            })

                            entries = realm?.where(Entry::class.java)?.findAll()
                            adapter.replaceAll(entries?.toList())*/

                            val service = createService(NotificateService::class.java)

                            listEntry.forEach {
                                subscriptions.add(service
                                        .notificate(LoginController.user?.phone ?: "",
                                                LoginController.user?.name ?: "",
                                                it.name ?: "",
                                                it.workTypes?.fold("") { acc, workTypeObject ->
                                                    "$acc${
                                                    if (TextUtils.equals(workTypeObject.name, allPhotoTypes[4].title)) {
                                                        if (workTypeObject.photos != null && workTypeObject.photos!!.isNotEmpty()) {
                                                            workTypeObject.name
                                                        } else {
                                                            ""
                                                        }
                                                    } else {
                                                        workTypeObject.name
                                                    }
                                                    }${if (it.workTypes?.indexOf(workTypeObject) != (it.workTypes?.size
                                                                    ?: 0) - 1) {
                                                        "+"
                                                    } else {
                                                        ""
                                                    }
                                                    }"
                                                } ?: "")
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            Toast.makeText(context, getString(R.string.send_file_notif_success), Toast.LENGTH_SHORT).show()
                                        }, {
                                            Toast.makeText(context, getString(R.string.send_file_notif_failed), Toast.LENGTH_SHORT).show()
                                            it.printStackTrace()
                                            //throw RuntimeException(getString(R.string.send_file_notif_failed))
                                        })
                                )
                            }

                        }, {
                            Toast.makeText(context, getString(R.string.send_file_failed), Toast.LENGTH_SHORT).show()
                            it.printStackTrace()
                            //throw RuntimeException(getString(R.string.send_file_notif_failed))
                        })
            } else if (checkedEntries.size == 0) {
                Toast.makeText(context, getString(R.string.send_file_no_checked), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addFiles(entries: List<EntryObject>?) {

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
        /*} catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

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

