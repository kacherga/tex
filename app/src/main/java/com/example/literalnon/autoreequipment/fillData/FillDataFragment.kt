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
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_fill_data.*
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.AddEntryFragment.Companion.choiceTypes
import services.mobiledev.ru.cheap.ui.main.comments.mvp.FillDataPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IFillDataPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IFillDataView
import java.io.File


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
    private val photoTypes = arrayListOf(PHOTO_TYPE.PHOTO_TYPE_1, PHOTO_TYPE.PHOTO_TYPE_2, PHOTO_TYPE.PHOTO_TYPE_3, PHOTO_TYPE.PHOTO_TYPE_4)

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
            dialogFilePicker()
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

        mainEntryTypeAdapter.manager?.addDelegate(MainEntryTaskDelegate { photo, pos ->
            currentPhoto = Pair(pos, photo)

            dialogFilePicker()
        })

        mainEntryTypeAdapter.manager?.addDelegate(MainEntryTypeDelegate())
        mainEntryTypeAdapter.manager?.addDelegate(PhotoTypeDelegate())

        rvPhotos.layoutManager = GridLayoutManager(context, 4)
        rvPhotos.adapter = photoAdapter

        rvTasks.layoutManager = LinearLayoutManager(context)
        rvTasks.adapter = mainEntryTypeAdapter

        val list = arrayListOf<Any>()
        list.addAll(choiceTypes)

        val photoIds = choiceTypes.fold(HashMap<Int, ArrayList<EntryType>>()) { arr: HashMap<Int, ArrayList<EntryType>>, entryType: EntryType ->
            entryType.photosId.forEach {
                if (arr.containsKey(it)) {
                    arr[it]?.add(entryType)
                } else {
                    arr[it] = arrayListOf(entryType)
                }
            }

            arr
        }

        photoTypes.forEach { photoType ->
            photoIds.filter { photos[it.key].type == photoType }.let {
                if (it.isNotEmpty()) {
                    list.add(photoType)
                    list.addAll(it.map {
                        photos[it.key].apply {
                            if (!isEdit) {
                                photo = null
                                workType = it.value
                            }
                        }
                    })
                }
            }
        }

        mainEntryTypeAdapter.replaceAll(list)
        rvTasks.isNestedScrollingEnabled = false

        tvName.text = EnterNameFragment.name

        btnNext.setOnClickListener {
            sendData()
            isNotSave = false
        }

        Log.d("tag", "isEdit : ${isEdit} : ${AddEntryFragment.extras != null}")

        if (isEdit && AddEntryFragment.extras != null) {
            etDescriptionTask.setText(AddEntryFragment.extras!!.name)

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
                currentPhoto?.second?.photo = file.path
                mainEntryTypeAdapter.notifyItemChanged(currentPhoto!!.first)
            } else {
                Toast.makeText(context, "file empty", Toast.LENGTH_LONG).show()
            }
        } else {
            if (file != null) {
                extraPhotos.add(file)
                photoAdapter.add(file)
                etFillPhotoHint.visibility = View.GONE
                Toast.makeText(context, "loaded", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "file empty", Toast.LENGTH_LONG).show()
            }

            if (extraPhotos.size >= 20) {
                ivAddPhoto.visibility = View.GONE
            }
        }
    }

    private fun dialogFilePicker() {
        /*val choose = resources.getStringArray(R.array.choose_image_dialog)

        AlertDialog.Builder(activity)
                .setTitle(getString(R.string.profile_edit_dialog_title_profile_pick_photo))
                .setItems(choose) { dialog, which ->*/
                    /*if (which == 0) {
                        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestStoragePermission(REQUEST_STORAGE_PERMISSION)
                        } else {
                            filePicker.requestGalleryIntent()
                        }
                    } else if (which == 1) {*/
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
                    //}
              /*  }
                .show()*/
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

    private fun sendData() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        if (isEdit) {
            realm?.where(Entry::class.java)?.`in`("name", arrayOf(EnterNameFragment.name))?.findAll()?.deleteAllFromRealm()
        }

        val currentEntry = realm.createObject(Entry::class.java)

        currentEntry.name = EnterNameFragment.name

        choiceTypes.forEach { entryType ->
            val workType = realm.createObject(WorkType::class.java)

            workType.name = entryType.title

            entryType.photosId.forEach {

                val mPhoto = RealmPhoto().apply {
                    name = photos[it].name
                    photo = photos[it].photo
                    type = photos[it].type.title
                    id = it
                }

                workType.photos?.add(realm.copyToRealm(mPhoto))
            }

            currentEntry.workTypes?.add(workType)
        }

        val workType = realm.createObject(WorkType::class.java)
        workType.name = PHOTO_TYPE.PHOTO_TYPE_4.title

        extraPhotos.forEach {
            val mPhoto = RealmPhoto().apply {
                name = "Доп. фото ${extraPhotos.indexOf(it)}"
                photo = it.path
                type = etDescriptionTask.text.toString()
                id = 0
            }

            workType.photos?.add(realm.copyToRealm(mPhoto))
        }

        currentEntry.workTypes?.add(workType)

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

