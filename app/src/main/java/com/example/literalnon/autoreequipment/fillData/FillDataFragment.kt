package services.mobiledev.ru.cheap.ui.main.comments

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
import android.util.Log
import com.example.literalnon.autoreequipment.*
import com.example.literalnon.autoreequipment.adapters.DelegationAdapter
import com.example.literalnon.autoreequipment.data.Entry
import com.example.literalnon.autoreequipment.data.RealmPhoto
import com.example.literalnon.autoreequipment.data.WorkType
import com.example.literalnon.autoreequipment.fillData.MainEntryTaskDelegate
import com.example.literalnon.autoreequipment.fillData.MainEntryTypeDelegate
import com.example.literalnon.autoreequipment.fillData.PhotoDelegate
import com.example.literalnon.autoreequipment.fillData.PhotoTypeDelegate
import com.example.literalnon.autoreequipment.utils.PermissionUtil
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_fill_data.*
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.AddEntryFragment.Companion.choiceTypes
import services.mobiledev.ru.cheap.ui.main.comments.mvp.FillDataPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IFillDataPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IFillDataView
import java.io.File
import java.lang.StringBuilder


class FillDataFragment : Fragment(), IFillDataView,
        MediaFilePicker.OnFilePickerListener {

    companion object {
        private val TAG = "TAG_REQUEST_FRAGMENT_SERVICES"

        private const val EXTRA_IS_EDIT = "isEdit"

        private const val REQUEST_STORAGE_PERMISSION = 11
        private const val REQUEST_CAMERA_PERMISSION = 22


        fun newInstance(isEdit: Boolean = false): FillDataFragment {
            return FillDataFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_IS_EDIT, isEdit)
                }
            }
        }
    }

    private var isNotSave = true
    private var isEdit = false

    override var presenter: IFillDataPresenter = FillDataPresenter()

    private var currentPhoto: Pair<Int, Photo>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_fill_data, container, false)

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }

    private lateinit var filePicker: MediaFilePicker
    private val photoAdapter = DelegationAdapter<Any>()
    private val extraPhotos = ArrayList<File>()
    private val photoTypes = arrayListOf(allPhotoTypes[1], allPhotoTypes[2], allPhotoTypes[3], allPhotoTypes[4])

    private val mainEntryTypeAdapter = DelegationAdapter<Any>()

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        ivAddPhoto.setOnClickListener {
            currentPhoto = null
            //dialogFilePicker()
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
                photos.find { item -> it.first == item.id }?.type == photoType
            }
                    .let {
                        if (it.isNotEmpty()) {
                            list.add(photoType)

                            it.forEach {
                                val item = photos.find { item -> it.first == item.id }

                                //Log.e("workTypes", "fill ${it.first} : ${item?.type} : ${item?.photos?.count()}")
                                item?.apply {
                                    if (!isEdit) {
                                        photos = null
                                        workType = it.second
                                    }
                                }

                                if (item != null) {
                                    list.add(item)
                                }
                            }
                        }
                    }
        }

        mainEntryTypeAdapter.replaceAll(list)
        rvTasks.isNestedScrollingEnabled = false

        tvName.text = EnterNameFragment.name + " " + EnterNameFragment.phone

        btnNext.setOnClickListener {
            sendData()
            isNotSave = false
        }

        //Log.d("tag", "isEdit : ${isEdit} : ${AddEntryFragment.extras != null}")

        if (isEdit && AddEntryFragment.extras != null) {
            val extraMap = Gson().fromJson<Map<String, String>>(AddEntryFragment.extras!!.name, HashMap<String, String>()::class.java)
            //etDescriptionTask.setText(AddEntryFragment.extras!!.name)

            etDistance.setText(extraMap[getString(R.string.fragment_fill_data_distance)])
            etTrunk.setText(extraMap[getString(R.string.fragment_fill_data_trunk)])
            etDescriptionTask.setText(extraMap[getString(R.string.fragment_fill_data_extra_title)])

            try {

                val photos = AddEntryFragment.extras!!.photos?.map {
                    File(it)
                }

                extraPhotos.addAll(photos ?: ArrayList())
                photoAdapter.addAll(photos)
                etFillPhotoHint.visibility = View.GONE
            } catch (e: Exception) {

            }
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

                    //currentPhoto?.second?.photos?.add(file.path)
                    photos[currentPhoto?.second?.id ?: 0].photos?.add(file.path)
                    //Log.e("change", "currentPhoto!!.first : ${currentPhoto!!.first}")
                } else {
                    //currentPhoto?.second?.photos?.set(0, file.path)
                    photos[currentPhoto?.second?.id ?: 0].photos?.set(0, file.path)
                }
                mainEntryTypeAdapter.notifyItemChanged(currentPhoto!!.first)
            } else {
                Toast.makeText(context, "file empty", Toast.LENGTH_LONG).show()
            }
        } else {
            if (file != null) {
                extraPhotos.add(file)
                photoAdapter.add(file)
                etFillPhotoHint.visibility = View.GONE
                //Toast.makeText(context, "loaded", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "file empty", Toast.LENGTH_LONG).show()
            }

            if (extraPhotos.size >= 20) {
                ivAddPhoto.visibility = View.GONE
            }
        }
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
        if (isNotSave) {
            sendData()
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

        val thisEntries = realm?.where(Entry::class.java)?.`in`("name", arrayOf(EnterNameFragment.name))?.`in`("phone", arrayOf(EnterNameFragment.phone))?.findAll()

        if (isEdit) {
            thisEntries?.deleteAllFromRealm()
        }

        val currentEntry: Entry = thisEntries?.find { it.phone == EnterNameFragment.phone } ?: {
            val entry = realm.createObject(Entry::class.java)

            entry.name = EnterNameFragment.name
            entry.phone = EnterNameFragment.phone
            entry
        }.invoke()

        choiceTypes.forEach { entryType ->
            val workType = realm.createObject(WorkType::class.java)

            workType.name = entryType.title

            entryType.photosId.forEach {
                photos[it].photos?.forEach { _photo ->

                    val mPhoto = RealmPhoto().apply {
                        name = photos[it].name
                        photo = _photo
                        type = photos[it].type.title
                        id = it
                    }

                    workType.photos?.add(realm.copyToRealm(mPhoto))
                }
            }

            val curWorkType = currentEntry.workTypes?.find { workType.name == it.name }

            if (curWorkType == null) {
                currentEntry.workTypes?.add(workType)
            } else {
                workType.photos?.forEach {
                    curWorkType.photos?.add(it)
                }
            }
        }

        val workType = realm.createObject(WorkType::class.java)
        workType.name = EXTRA_PHOTO_TITLE

        val extraMap = HashMap<String, String>()
        extraMap[getString(R.string.fragment_fill_data_trunk)] = etTrunk.text?.toString() ?: ""
        extraMap[getString(R.string.fragment_fill_data_distance)] = etDistance.text?.toString() ?: ""
        extraMap[getString(R.string.fragment_fill_data_extra_title)] = etDescriptionTask.text?.toString() ?: ""

        workType.description = Gson().toJson(extraMap)

        extraPhotos.forEachIndexed { index, it ->
            val mPhoto = RealmPhoto().apply {
                name = "${EXTRA_PHOTO_TITLE}_$index"
                photo = it.path
                type = ""
                id = index
            }

            workType.photos?.add(realm.copyToRealm(mPhoto))
        }

        val curWorkType = currentEntry.workTypes?.find { workType.name == it.name }

        if (curWorkType == null) {
            currentEntry.workTypes?.add(workType)
        } else {
            workType.photos?.forEach {
                curWorkType.photos?.add(it)
            }

            curWorkType.description = workType.description
        }

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

