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
import com.example.literalnon.autoreequipment.EXTRA_PHOTO_TITLE
import com.example.literalnon.autoreequipment.R
import com.example.literalnon.autoreequipment.allPhotoTypes
import com.example.literalnon.autoreequipment.data.Entry
import com.example.literalnon.autoreequipment.data.EntryObject
import com.example.literalnon.autoreequipment.data.PhotoObject
import com.example.literalnon.autoreequipment.data.WorkTypeObject
import com.example.literalnon.autoreequipment.network.NotificateService
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.data.Prefs
import services.mobiledev.ru.cheap.ui.main.comments.EnterNameFragment
import java.io.File
import java.util.*


class UpdateService : Service()/*IntentService("intentServiceName")*/ {
    private var link: String? = ""

    private var notificationBuilder: NotificationCompat.Builder? = null

    companion object {
        const val IS_FROM_SERVICE = "IS_FROM_SERVICE"
        const val UPLOADING_APK_NOTIFICATION_ID = 2
        const val UPLOADING_APK_CHANNEL_ID = "UPLOADING_APK_CHANNEL_ID"

        const val IMAGE_MAX_SIZE = 640
        const val IMAGE_COMPRESS_QUALITY = 75
        const val IMAGE_COMPRESSED_NAME = "compressedImage"
        const val IMAGE_COMPRESSED_EXTENSION = ".jpg"

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
        //Log.e("updater", "init")
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
                Log.e("UpdateService", "list : ${list.size} : ${list}")
                uploadFiles(list, this)
            }
        }

        return START_REDELIVER_INTENT
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createDownloadNotification(progress: Int? = null): Notification {
        ////Log.e("updater", "createDownloadNotification")

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

        //Log.e("updater", "createDownloadNotificationEnd")
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
            //////Log.e("makeDirectory", "realm = Realm.getDefaultInstance() size : ${listEntry.size} : ${checkedEntries.size}")
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
                                        it.phone ?: "",
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

        ////Log.e("makeDirectory", "login")

        if (ftpClient.login(getString(R.string.ftp_login), getString(R.string.ftp_password))) {
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

            val companyName = "${LoginController.user?.phone
                    ?: "Без имени"}"


            ////Log.e("makeDirectory", companyName + " : " + ftpClient.controlEncoding)

            //ftpClient.makeDirectory((LoginController.user?.town ?: "Без города"))
            //ftpClient.makeDirectory(companyName)

            val maxProgress = entries?.fold(0) { acc, item ->
                acc + (item.workTypes?.fold(0) { acc, item ->
                    acc + (item.photos?.count() ?: 0)
                } ?: 0)
            } ?: 0

            var progress = 0

            entries?.forEach { entry ->
                if (entry.sendedAt == null) {
                    entry.sendType = 0

                    val path = "$companyName/${entry.name?.lines()?.fold("") { acc, s ->
                        "$acc $s"
                    }?.trim()}"

                    val entryPath = path + "/${entry.phone}"

                    //ftpClient.makeDirectory(path)
                    ////Log.e("makeDirectory", "${path}")
                    entry.workTypes?.forEach {
                        if (it.sendedAt == null) {
                            try {
                                if (it.description?.isNotEmpty() == true) {
                                    //Log.e("makeDirectory", "it.description : ${entryPath + "/" + EXTRA_PHOTO_TITLE + ".txt"}")
                                    ftpClient.makeDirectory(companyName)
                                    ftpClient.makeDirectory(path)
                                    ftpClient.makeDirectory(entryPath)
                                    ftpClient.appendFile(entryPath + "/" + EXTRA_PHOTO_TITLE + ".txt", it.description?.byteInputStream())// "/" + it.type + "/" + (it.name ?: "photo") +
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            //ftpClient.makeDirectory(workTypePath)
                            //Log.e("makeDirectory", "${it.description} : ${it.name}")

                            it.photos?.forEachIndexed { index, it ->
                                if (it.sendedAt == null) {
                                    //ftpClient.makeDirectory(workTypePath + "/" + it.type)
                                    ////Log.e("appendFile", "${workTypePath} ${it.photo}")// + "/" + it.type + "/" + it.name
                                    if (it.photo != null) {
                                        try {
                                            //ftpClient.makeDirectory((LoginController.user?.phone
                                            //      ?: "Без имени"))
                                            val compressedFile = BitmapUtils.compressImage(this, File(it.photo), IMAGE_MAX_SIZE, IMAGE_COMPRESSED_NAME + Calendar.getInstance().timeInMillis + IMAGE_COMPRESSED_EXTENSION, IMAGE_COMPRESS_QUALITY)
                                            if (compressedFile != null) {
                                                ftpClient.makeDirectory(companyName)
                                                ftpClient.makeDirectory(path)
                                                ftpClient.makeDirectory(entryPath)
                                                //ftpClient.makeDirectory(workTypePath)
                                                //ftpClient.makeDirectory(workTypePath + "/" + it.type)
                                                ftpClient.appendFile(entryPath + "/" + "${it.type}_${it.name}_${index}" + IMAGE_COMPRESSED_EXTENSION, compressedFile?.inputStream())// "/" + it.type + "/" + (it.name ?: "photo") +
                                                it.sendedAt = Calendar.getInstance().timeInMillis
                                                entry.sendType = 2
                                                saveDate(entry)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    progress += 1

                                    notificationBuilder = NotificationCompat.Builder(this)
                                            //.setContentIntent(getPendingIntentForLoadUpdate(context, link))//Intent(context, NavigationDrawerActivity::class.java)
                                            .setSmallIcon(R.drawable.ic_launcher)
                                            //.setWhen(System.currentTimeMillis())
                                            .setChannelId(UPLOADING_APK_CHANNEL_ID)
                                            .setDefaults(0)
                                            .setSound(null)
                                            .setVibrate(null)
                                            .setContentTitle(getString(R.string.send_photos))
                                            .setProgress(100, progress * 100 / maxProgress, progress == null)
                                    //.build()
                                    val notification = notificationBuilder?.build()!!

                                    notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

                                    notificationSystemManager?.notify(UPLOADING_APK_NOTIFICATION_ID, notification)
                                }
                            }

                            it.sendedAt = Calendar.getInstance().timeInMillis
                        }
                    }

                    entry.sendedAt = Calendar.getInstance().timeInMillis
                    entry.sendType = 1
                }
            }

            ftpClient.logout()
            ftpClient.disconnect()

            sendData(entries)

        }
        /*} catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    private fun saveDate(entry: EntryObject) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val elem = realm?.where(Entry::class.java)?.`in`("name", arrayOf(entry.name))?.findFirst()
        elem?.sendedAt = entry.sendedAt
        elem?.sendType = entry.sendType

        realm.commitTransaction()

        realm.executeTransaction({ bgRealm ->

        })

        realm.close()
    }

    private fun sendData(entries: Array<EntryObject>?) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        entries?.forEach {
            val elem = realm?.where(Entry::class.java)?.`in`("name", arrayOf(it.name))?.findFirst()
            elem?.sendedAt = it.sendedAt
            elem?.sendType = it.sendType
        }

        realm.commitTransaction()

        realm.executeTransaction({ bgRealm ->

        })

        realm.close()
    }

    fun showInstallNotification(context: Context, intent: PendingIntent) {

        //Log.e("updater", "STart NOTIFY!!!")
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