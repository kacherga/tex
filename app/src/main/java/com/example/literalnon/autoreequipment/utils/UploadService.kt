package com.example.literalnon.autoreequipment.utils

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.app.NotificationManager
import android.graphics.Color
import android.text.TextUtils
import android.widget.Toast
import com.betcityru.dyadichko_da.betcityru.ui.createService
import com.example.literalnon.autoreequipment.R
import com.example.literalnon.autoreequipment.allPhotoTypes
import com.example.literalnon.autoreequipment.data.EntryObject
import com.example.literalnon.autoreequipment.data.PhotoObject
import com.example.literalnon.autoreequipment.data.WorkTypeObject
import com.example.literalnon.autoreequipment.network.NotificateService
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.data.Prefs
import java.io.File
import java.util.ArrayList


class UpdateService : Service()/*IntentService("intentServiceName")*/ {
    private var link: String? = ""

    private var notificationBuilder: NotificationCompat.Builder? = null

    companion object {
        const val IS_FROM_SERVICE = "IS_FROM_SERVICE"
        const val UPLOADING_APK_NOTIFICATION_ID = 2
        const val UPLOADING_APK_CHANNEL_ID = "UPLOADING_APK_CHANNEL_ID"

        const val EXTRA_JSON = "json"

        var service: Service? = null

        fun getMyService(): Service? {
            return service
        }

        private val EXTRA_IS_DOWNLOADING = "EXTRA_IS_DOWNLOADING"

        const val TRY_COUNT = 20

        var notificationSystemManager: NotificationManager? = null

        var isDownloading = false
            get() {
                if (field == false) {
                    field = Prefs.load(EXTRA_IS_DOWNLOADING, Boolean::class.java) ?: false
                }

                return field
            }
            set(value) {
                field = value

                Prefs.save(EXTRA_IS_DOWNLOADING, field)
            }
    }

    init {
        Log.e("updater", "init")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("updater", "onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mIntent = Intent(this, UpdateService::class.java)
            //mIntent.action = intent?.action
            mIntent.putExtra(IS_FROM_SERVICE, true)
            startForegroundService(mIntent)
        }
    }

    override fun onDestroy() {
        Log.d("updater", "onDestroy")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.getBooleanExtra(IS_FROM_SERVICE, false) == true) {
            //stopSelf()
            Log.d("updater", "intent?.getBooleanExtra(IS_FROM_SERVICE, false) == true")
        } else {

            notificationSystemManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            startForeground(UPLOADING_APK_NOTIFICATION_ID, createDownloadNotification())

            service = this

            link = intent?.action

            Log.d("updater", "onStartCommand link: ${link} : ${isDownloading}")

            if (!isDownloading) {
                val list = Gson().fromJson(intent?.extras?.getString(EXTRA_JSON), Array<EntryObject>::class.java)
                uploadFiles(list, this)
            }
        }

        return START_REDELIVER_INTENT
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createDownloadNotification(progress: Int? = null): Notification {
        //Log.e("updater", "createDownloadNotification")

        if (Build.VERSION.SDK_INT > 25) {
            createNotificationChannel(this, UPLOADING_APK_CHANNEL_ID)
        }

        notificationBuilder = NotificationCompat.Builder(this)
                //.setContentIntent(getPendingIntentForLoadUpdate(context, link))//Intent(context, NavigationDrawerActivity::class.java)
                .setSmallIcon(R.drawable.ic_launcher)
                //.setWhen(System.currentTimeMillis())
                .setChannelId(UPLOADING_APK_CHANNEL_ID)
                .setDefaults(0)
                .setSound(null)
                .setVibrate(null)
                .setContentTitle(getString(R.string.send_photos))
                .setProgress(100, progress ?: 0, progress == null)
        //.build()
        val notification = notificationBuilder?.build()!!

        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        notificationSystemManager?.notify(UPLOADING_APK_NOTIFICATION_ID, notification)

        Log.e("updater", "createDownloadNotificationEnd")
        return notification
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, id: String) {
        notificationSystemManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val name = "myUserChannel"
        val description = "description"

        val importance = NotificationManager.IMPORTANCE_LOW

        val mChannel = NotificationChannel(id, name, importance)

        mChannel.description = description
        mChannel.setSound(null, null)

        mChannel.enableVibration(false)

        notificationSystemManager?.createNotificationChannel(mChannel)
    }

    private fun uploadFiles(listEntry: Array<EntryObject>, context: Context) {

        Observable.create<Unit> {
            ////Log.e("makeDirectory", "realm = Realm.getDefaultInstance() size : ${listEntry.size} : ${checkedEntries.size}")
            it.onNext(addFiles(listEntry))

            it.onComplete()
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    //dismissLoading()
                    //listEntry.clear()
                    isDownloading = false
                    stopSelf()
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
                        //subscriptions.add(
                                service
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
                        //)
                    }

                }, {
                    Toast.makeText(context, getString(R.string.send_file_failed), Toast.LENGTH_SHORT).show()
                    it.printStackTrace()
                    //throw RuntimeException(getString(R.string.send_file_notif_failed))
                })
    }

    private fun addFiles(entries: Array<EntryObject>?) {

        //try {
        val ftpClient = FTPClient()
        ftpClient.controlEncoding = "UTF-8"

        ftpClient.connect(getString(R.string.ftp_client))

        //Log.e("makeDirectory", "login")

        if (ftpClient.login(getString(R.string.ftp_login), getString(R.string.ftp_password))) {
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

            val companyName = "${LoginController.user?.phone
                    ?: "Без имени"}"

            //Log.e("makeDirectory", companyName + " : " + ftpClient.controlEncoding)

            //ftpClient.makeDirectory((LoginController.user?.town ?: "Без города"))
            //ftpClient.makeDirectory(companyName)

            entries?.forEach {
                val path = "$companyName/${it.name?.lines()?.fold("") { acc, s ->
                    "$acc $s"
                }?.trim()}/${it.phone}"
                //ftpClient.makeDirectory(path)
                Log.e("makeDirectory", "${path}")
                it.workTypes?.forEach {
                    val workTypePath = path + "/" + it.name
                    //ftpClient.makeDirectory(workTypePath)
                    Log.e("makeDirectory", "${workTypePath}")
                    it.photos?.forEach {
                        //ftpClient.makeDirectory(workTypePath + "/" + it.type)
                        Log.e("appendFile", "${workTypePath} ${it.photo}")// + "/" + it.type + "/" + it.name
                        if (it.photo != null)
                            try {
                                //ftpClient.makeDirectory((LoginController.user?.phone
                                  //      ?: "Без имени"))
                                ftpClient.makeDirectory(companyName)
                                ftpClient.makeDirectory(path)
                                ftpClient.makeDirectory(workTypePath)
                                ftpClient.makeDirectory(workTypePath + "/" + it.type)
                                ftpClient.appendFile(workTypePath + ".jpg", File(it.photo).inputStream())// "/" + it.type + "/" + (it.name ?: "photo") +
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

    fun showInstallNotification(context: Context, intent: PendingIntent) {

        Log.e("updater", "STart NOTIFY!!!")
        Thread.sleep(200)

        notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_bottom_navigation_add_entry)
                .setContentIntent(intent)
                .setContentTitle("Установка")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(null)
                .setContentText(context.getString(R.string.title_home))
                .setChannelId(UPLOADING_APK_CHANNEL_ID)
                .setContentTitle(getString(R.string.title_dashboard))

        val notification = notificationBuilder!!.build()!!

        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        notificationSystemManager!!.notify(UPLOADING_APK_NOTIFICATION_ID, notification)
    }


}