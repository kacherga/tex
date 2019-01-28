package com.example.literalnon.autoreequipment.fillData

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import com.example.literalnon.autoreequipment.*
import com.example.literalnon.autoreequipment.adapters.DelegationAdapter
import com.example.literalnon.autoreequipment.data.*
import com.example.literalnon.autoreequipment.fillData.MainEntryTaskDelegate
import com.example.literalnon.autoreequipment.fillData.MainEntryTypeDelegate
import com.example.literalnon.autoreequipment.fillData.PhotoDelegate
import com.example.literalnon.autoreequipment.fillData.PhotoTypeDelegate
import com.example.literalnon.autoreequipment.utils.PermissionUtil
import com.google.gson.Gson
import io.realm.Realm
import com.google.gson.reflect.TypeToken
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_fill_data.*
import org.w3c.dom.Text
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.FillDataPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IFillDataPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IFillDataView
import java.io.File
import java.lang.StringBuilder

data class FillDataItem(
        val name: String,
        val phone: String,
        val choiceType: ArrayList<EntryType>,
        val extras: Extras?,
        val isNeedLoadEntry: Boolean? = null
)

class FillDataFragment : Fragment(), IFillDataView,
        MediaFilePicker.OnFilePickerListener {

    companion object {
        private val TAG = "TAG_REQUEST_FRAGMENT_SERVICES"

        private const val EXTRA_IS_EDIT = "isEdit"
        private const val EXTRA_CHOICE_TYPE = "choiceType"
        private const val EXTRA_EXTRA = "extras"

        private const val EXTRA_NAME = "name"
        private const val EXTRA_PHONE = "phone"

        private const val EXTRA_ENTRY = "entry"

        private const val REQUEST_STORAGE_PERMISSION = 11
        private const val REQUEST_CAMERA_PERMISSION = 22


        fun newInstance(isEdit: Boolean = false, dataItem: FillDataItem): FillDataFragment {
            return FillDataFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_EDIT, isEdit)
                    putString(EXTRA_NAME, dataItem.name)
                    putString(EXTRA_PHONE, dataItem.phone)
                    putString(EXTRA_CHOICE_TYPE, Gson().toJson(dataItem.choiceType))
                    if (dataItem.extras != null) {
                        //Log.e("tag", "extras! : ${Gson().toJson(dataItem.extras)} : ${dataItem.extras}")
                        putString(EXTRA_EXTRA, Gson().toJson(dataItem.extras))
                    }

                    if (dataItem.isNeedLoadEntry == true) {
                        putBoolean(EXTRA_ENTRY, dataItem.isNeedLoadEntry)
                    }
                }
            }
        }
    }

    private var isNotSave = true
    private var isEdit = false

    override var presenter: IFillDataPresenter = FillDataPresenter()

    private var currentPhoto: Pair<Int, Photo>? = null
    private var currentPhotos = arrayListOf(
            Photo(0, "Неизвестный тип", allPhotoTypes[2]),
            Photo(1, "Общий вид автомобиля спереди с Гос. Знаком", allPhotoTypes[3]),
            Photo(2, "Общий вид сбоку", allPhotoTypes[3]),
            Photo(3, "Общий вид автомобиля сзади с Гос. знаком", allPhotoTypes[3]),
            Photo(4, "Подкапотное пространство", allPhotoTypes[3]),
            Photo(5, "Редуктор", allPhotoTypes[3]),
            Photo(6, "Блок управления", allPhotoTypes[3]),
            Photo(7, "Форсунки", allPhotoTypes[3]),
            Photo(8, "Газовый клапан (если есть)", allPhotoTypes[3]),
            Photo(9, "Бензиновый клапан (если есть)", allPhotoTypes[3]),
            Photo(10, "Проводка (должна быть в гофрированном кожухе)", allPhotoTypes[3]),
            Photo(11, "Баллон (на расстоянии)", allPhotoTypes[3], 8),
            Photo(12, "Бирка баллона (должен быть отчетливо виден номер)", allPhotoTypes[2], 8),
            Photo(13, "Вентиляция", allPhotoTypes[3]),
            Photo(14, "Мультиклапан", allPhotoTypes[3]),
            Photo(15, "ВЗУ", allPhotoTypes[3]),
            Photo(16, "Паспорт баллона", allPhotoTypes[2], 8),
            Photo(17, "Паспорт собственника первая страница", allPhotoTypes[1]),
            Photo(18, "Прописка собственника", allPhotoTypes[1]),
            Photo(19, "Паспорт представителя первая страница", allPhotoTypes[1]),
            Photo(20, "Прописка представителя", allPhotoTypes[1]),
            Photo(21, "Свидетельство регистрации автомобиля лицевая сторона", allPhotoTypes[1]),
            Photo(22, "Свидетельство регистрации автомобиля обратная сторона", allPhotoTypes[1]),
            Photo(23, "ПТС лицевая сторона", allPhotoTypes[1]),
            Photo(24, "ПТС обратная сторона", allPhotoTypes[1]),
            Photo(25, "Крупный план силового бампера", allPhotoTypes[3]),
            Photo(26, "Сертификат на силовой бампер", allPhotoTypes[2]),
            Photo(27, "Приложение к сертификату с указанием конкретной модели авто.", allPhotoTypes[2]),
            Photo(28, "Фаркоп (ТСУ) крупным планом", allPhotoTypes[3]),
            Photo(29, "Заводская маркировка Фаркопа (ТСУ) крупный план", allPhotoTypes[3]),
            Photo(30, "Паспорт на Фаркоп (ТСУ)", allPhotoTypes[2]),
            Photo(31, "Наружный блок холодильной установки", allPhotoTypes[3]),
            Photo(32, "Внутренний блок холодильной установки", allPhotoTypes[3]),
            Photo(33, "Крупный план заводской маркировки холодильной установки", allPhotoTypes[3]),
            Photo(34, "Общий вид внутреннего пространства кузова с местом расположения внутреннего блока", allPhotoTypes[3]),
            Photo(35, "Крупный план компрессора холодильной установки", allPhotoTypes[3]),
            Photo(36, "Паспорт на холодильную установку", allPhotoTypes[2]),
            Photo(37, "Сертификат на автомобильную установку", allPhotoTypes[2]),
            Photo(38, "Элементы крепления Двигателя в подкапотном пространстве", allPhotoTypes[3]),
            Photo(39, "Крупный план заводского номера Двигателя", allPhotoTypes[3]),
            Photo(40, "Фото розетки (электрики)", allPhotoTypes[3]),
            Photo(41, "Паспорт ДВС", allPhotoTypes[2]),
            Photo(42, "Паспорт ДКП", allPhotoTypes[2]),
            Photo(43, "Паспорт ГТД", allPhotoTypes[2]),
            Photo(44, "ПТС донора", allPhotoTypes[2])
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_fill_data, container, false)

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }

    private lateinit var filePicker: MediaFilePicker
    private val photoAdapter = DelegationAdapter<Any>()
    private var extraPhotos = ArrayList<File>()
    private var photoTypes = arrayListOf(allPhotoTypes[1], allPhotoTypes[2], allPhotoTypes[3], allPhotoTypes[4])

    private val mainEntryTypeAdapter = DelegationAdapter<Any>()

    private lateinit var choiceTypes: ArrayList<EntryType>
    private var extras: Extras? = null

    private var name: String? = null
    private var phone: String? = null

    private var entry: EntryObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filePicker = MediaFilePicker.Builder().activity(activity)
                .fragment(this)
                .listener(this)
                .activity(activity)
                .savedState(savedInstanceState)
                .build()

        if (arguments?.containsKey(EXTRA_IS_EDIT) == true) {
            isEdit = arguments!!.getBoolean(EXTRA_IS_EDIT)
        }

        if (arguments?.containsKey(EXTRA_CHOICE_TYPE) == true) {
            choiceTypes = Gson().fromJson(arguments!!.getString(EXTRA_CHOICE_TYPE), object : TypeToken<ArrayList<EntryType>>() {}.type)
        }

        if (arguments?.containsKey(EXTRA_EXTRA) == true) {
            //Log.e("tag", "extras : ${arguments!!.getString(EXTRA_EXTRA)}")
            extras = Gson().fromJson(arguments!!.getString(EXTRA_EXTRA), Extras::class.java)
        }

        if (arguments?.containsKey(EXTRA_NAME) == true) {
            name = arguments!!.getString(EXTRA_NAME)
        }

        if (arguments?.containsKey(EXTRA_PHONE) == true) {
            phone = arguments!!.getString(EXTRA_PHONE)
        }

        if (arguments?.containsKey(EXTRA_ENTRY) == true) {
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()

            Log.e("TAG", "${Gson().toJson(realm
                    ?.where(Entry::class.java)
                    ?.`in`("name", arrayOf(name))
                    ?.`in`("phone", arrayOf(phone))
                    ?.findAll()
                    ?.count())}")

            entry = realm
                    ?.where(Entry::class.java)
                    ?.`in`("name", arrayOf(name))
                    ?.`in`("phone", arrayOf(phone))
                    ?.findFirst()
                    ?.toObject()

            Log.e("TAG", "${Gson().toJson(entry)}")

            realm.commitTransaction()

            realm.executeTransaction({ bgRealm ->

            })

            realm.close()

            if (entry != null) {
                entry?.photos?.forEach {
                    if (it.photoId == EXTRA_PHOTO_ID) {
                        extraPhotos.add(File(it.photo))
                    } else {
                        if (it.photo != null) {
                            if (currentPhotos[it.photoId ?: 0].photos == null) {
                                currentPhotos[it.photoId ?: 0].photos = arrayListOf()
                            }
                            currentPhotos[it.photoId ?: 0].photos?.add(it.photo!!)
                        }
                    }
                }

                entry?.workTypes?.forEach {
                    if (!TextUtils.equals(it.name, EXTRA_PHOTO_TITLE)) {
                        val findEntryType = allEntryType.find { entryType -> TextUtils.equals(it.name, entryType.title) }
                        if (findEntryType != null) {
                            choiceTypes.add(findEntryType)
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        entry?.workTypes?.find { TextUtils.equals(it.name, EXTRA_PHOTO_TITLE) }?.let {
            val extraMap = Gson().fromJson<HashMap<String, String>>(it.description, HashMap<String, String>()::class.java)

            if (extraMap[getString(R.string.fragment_fill_data_trunk)] != null) {
                etTrunk.setText(extraMap[getString(R.string.fragment_fill_data_trunk)])
            }

            if (extraMap[getString(R.string.fragment_fill_data_distance)] != null) {
                etDistance.setText(extraMap[getString(R.string.fragment_fill_data_distance)]
                        ?: "")
            }

            if (extraMap[getString(R.string.fragment_fill_data_extra_title)] != null) {
                etDescriptionTask.setText(extraMap[getString(R.string.fragment_fill_data_extra_title)]
                        ?: "")
            }
        }

        presenter.attachView(this)

        ivAddPhoto.setOnClickListener {
            currentPhoto = null
            dialogFilePickerWithGallery()
        }

        photoAdapter.manager?.addDelegate(PhotoDelegate { photo ->
            photoAdapter.remove(photoAdapter.items.indexOf(photo))
            extraPhotos.remove(photo)

            if (extraPhotos.isEmpty()) {
                etFillPhotoHint.visibility = View.VISIBLE
            }

            if (extraPhotos.size < 20) {
                ivAddPhoto.visibility = View.VISIBLE
            }
        })

        mainEntryTypeAdapter.manager?.addDelegate(MainEntryTaskDelegate({ photo, pos ->
            currentPhoto = Pair(pos, photo)

            dialogFilePicker()
        }, {
            android.support.v7.app.AlertDialog.Builder(context!!)
                    .setTitle("Фото")
                    .setItems(arrayOf("Открыть", "Удалить")) { _, i ->
                        if (i == 0) {
                            presenter.openPhoto(it.photos?.firstOrNull() ?: "")
                        } else {
                            if ((currentPhotos[it.id].photos?.count() ?: 0) > 0) {
                                currentPhotos[it.id].photos?.removeAt(0)
                                mainEntryTypeAdapter.notifyDataSetChanged()
                                sendData()
                            }
                        }
                    }
                    .show()
        }, {
            presenter.openPhoto(it)
        }))

        mainEntryTypeAdapter.manager?.addDelegate(MainEntryTypeDelegate())
        mainEntryTypeAdapter.manager?.addDelegate(PhotoTypeDelegate())

        rvPhotos.layoutManager = GridLayoutManager(context, 4)
        rvPhotos.adapter = photoAdapter

        rvTasks.layoutManager = LinearLayoutManager(context)
        rvTasks.adapter = mainEntryTypeAdapter

        val list = arrayListOf<Any>()
        list.addAll(choiceTypes)

        val photoIds = choiceTypes.fold(ArrayList<Pair<Int, ArrayList<EntryType>>>()) { arr: ArrayList<Pair<Int, ArrayList<EntryType>>>, entryType: EntryType ->
            entryType.photosId.forEach { item ->
                val elem = arr.find { it.first == item }

                if (elem != null) {
                    elem.second.add(entryType)
                } else {
                    arr.add(Pair(item, arrayListOf(entryType)))
                }
            }

            arr
        }

        photoTypes.forEach { photoType ->
            photoIds.filter {
                currentPhotos.find { item -> it.first == item.id }?.type == photoType
            }
                    .let {
                        if (it.isNotEmpty()) {
                            list.add(photoType)

                            it.forEach {
                                val item = currentPhotos.find { item -> it.first == item.id }

                                /*item?.apply {
                                    if (!isEdit) {
                                        photos = null
                                        workType = it.second
                                    }
                                }*/

                                if (item != null) {
                                    list.add(item)
                                }
                            }
                        }
                    }
        }

        mainEntryTypeAdapter.replaceAll(list)
        rvTasks.isNestedScrollingEnabled = false

        tvName.text = "${name} ${phone}"

        btnNext.setOnClickListener {
            sendData()
            //isNotSave = false
        }

        if (isEdit && extraPhotos.isNotEmpty()) {
            photoAdapter.addAll(extraPhotos.toList())
            etFillPhotoHint.visibility = View.GONE
        }
    }

    override fun onFilePicked(file: File?) {
        if (currentPhoto != null) {
            if (file != null) {
                if (currentPhoto?.second?.photos == null) {
                    currentPhoto?.second?.photos = ArrayList()
                }

                if ((currentPhoto?.second?.photoCount ?: 1) > (currentPhoto?.second?.photos?.size
                                ?: 0)) {
                    currentPhotos[currentPhoto?.second?.id ?: 0].photos?.add(file.path)
                } else {
                    currentPhotos[currentPhoto?.second?.id ?: 0].photos?.set(0, file.path)
                }

                mainEntryTypeAdapter.notifyDataSetChanged()//currentPhoto!!.first
            } else {
                Toast.makeText(context, "file empty", Toast.LENGTH_LONG).show()
            }
        } else {
            if (file != null) {
                extraPhotos.add(file)
                photoAdapter.add(file)
                etFillPhotoHint.visibility = View.GONE
            } else {
                Toast.makeText(context, "file empty", Toast.LENGTH_LONG).show()
            }

            if (extraPhotos.size >= 20) {
                ivAddPhoto.visibility = View.GONE
            }
        }

        sendData()
    }

    private fun dialogFilePicker() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestCameraAndStoragePermission()
            } else {
                requestCameraPermission()
            }
        } else if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission(REQUEST_CAMERA_PERMISSION)
        } else {
            filePicker.requestCameraIntent()
        }
    }

    private fun dialogFilePickerWithGallery() {
        val choose = arrayOf("Выбрать из галереи", "Сделать")

        android.support.v7.app.AlertDialog.Builder(context!!)
                .setTitle("Фото")
                .setItems(choose) { _, i ->
                    if (i == 0) {
                        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestStoragePermission(REQUEST_CAMERA_PERMISSION)
                        } else {
                            filePicker.requestGalleryIntent()
                        }
                    } else {
                        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestCameraPermission()
                        } else {
                            filePicker.requestCameraIntent()
                        }
                    }
                }
                .show()
    }

    private fun requestCameraPermission() {
        requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION)
    }

    private fun requestStoragePermission(code: Int) {
        requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE),
                code)
    }

    private fun requestCameraAndStoragePermission() {
        requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        filePicker.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        Log.e("tag", "onDestroyView")
        if (isNotSave) {
            //sendData()
        }

        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(MediaFilePicker.EXTRA_CUR_FILE_PATH, filePicker.saveState())
        super.onSaveInstanceState(outState)
    }

    private fun sendData() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val thisEntries = realm
                ?.where(Entry::class.java)
                ?.`in`("name", arrayOf(name))
                ?.`in`("phone", arrayOf(phone))
                ?.findAll()

        //if (isEdit) {
        thisEntries?.deleteAllFromRealm()

        realm.commitTransaction()

        realm.executeTransaction({ bgRealm ->

        })
        realm.beginTransaction()
        //}

        val currentEntry: Entry = realm.createObject(Entry::class.java)

        currentEntry.name = name
        currentEntry.phone = phone

        currentPhotos.forEach {
            it.photos?.forEach { _photo ->
                val realmPhoto = realm.createObject(RealmPhoto::class.java)

                realmPhoto.name = currentPhotos[it.id].name
                realmPhoto.photo = _photo
                realmPhoto.type = currentPhotos[it.id].type.title
                realmPhoto.photoId = it.id

                currentEntry.photos!!.add(realmPhoto)
            }
        }

        choiceTypes.forEach { entryType ->
            val workType = realm.createObject(WorkType::class.java)

            workType.name = entryType.title
            workType.photosId!!.clear()
            workType.photosId!!.addAll(entryType.photosId)
            /*entryType.photosId.forEach {
                currentPhotos[it].photos?.forEach { _photo ->
                    val realmPhoto = realm.createObject(RealmPhoto::class.java)

                    realmPhoto.name = currentPhotos[it].name
                    realmPhoto.photo = _photo
                    realmPhoto.type = currentPhotos[it].type.title
                    realmPhoto.photoId = it

                    workType.photos!!.add(realmPhoto)
                }
            }*/

            //val curWorkType = currentEntry.workTypes?.find { TextUtils.equals(workType.name, it.name) }

            //if (curWorkType == null) {
            currentEntry.workTypes!!.add(workType)
            /*} else {
                workType.photos?.forEach {
                    curWorkType.photos?.add(it)
                }
            }*/
        }

        val workType = realm.createObject(WorkType::class.java)
        workType.name = EXTRA_PHOTO_TITLE

        val extraMap = HashMap<String, String>()
        extraMap[getString(R.string.fragment_fill_data_trunk)] = etTrunk.text?.toString() ?: ""
        extraMap[getString(R.string.fragment_fill_data_distance)] = etDistance.text?.toString() ?: ""
        extraMap[getString(R.string.fragment_fill_data_extra_title)] = etDescriptionTask.text?.toString() ?: ""

        workType.description = Gson().toJson(extraMap)
        workType.photosId!!.add(EXTRA_PHOTO_ID)

        extraPhotos.forEachIndexed { index, it ->
            val mPhoto = realm.createObject(RealmPhoto::class.java)

            mPhoto.name = "${EXTRA_PHOTO_TITLE}_$index"
            mPhoto.photo = it.path
            mPhoto.type = ""
            mPhoto.photoId = EXTRA_PHOTO_ID

            currentEntry.photos!!.add(mPhoto)
        }

        //val curWorkType = currentEntry.workTypes?.find { workType.name == it.name }

        //if (curWorkType == null) {
        currentEntry.workTypes!!.add(workType)
        /*} else {
            */
        /**  удаление елементов из предыдущего примечания **//*
            curWorkType.photos?.clear()

            workType.photos?.forEach {
                //curWorkType.photos?.removeAll(curWorkType.photos?.filter { cur -> TextUtils.equals(it.name, cur.name) } ?: arrayListOf())
                curWorkType.photos?.add(it)
            }

            curWorkType.description = workType.description
        }*/

        Log.e("TAG", "2: ${Gson().toJson(currentEntry.toObject())}")

        realm.commitTransaction()

        realm.executeTransaction({ bgRealm ->

        })


        realm.close()

        Toast.makeText(context, "Сохранено", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                filePicker.requestGalleryIntent()
            } else {

            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                filePicker.requestCameraIntent()
            } else {

            }
        }
    }
}

