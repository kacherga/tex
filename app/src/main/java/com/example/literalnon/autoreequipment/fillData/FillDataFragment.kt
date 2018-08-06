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
import com.example.literalnon.autoreequipment.*
import com.example.literalnon.autoreequipment.adapters.DelegationAdapter
import com.example.literalnon.autoreequipment.fillData.MainEntryTaskDelegate
import com.example.literalnon.autoreequipment.fillData.MainEntryTypeDelegate
import com.example.literalnon.autoreequipment.fillData.PhotoDelegate
import com.example.literalnon.autoreequipment.fillData.PhotoTypeDelegate
import com.example.literalnon.autoreequipment.utils.PermissionUtil
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

        private const val REQUEST_STORAGE_PERMISSION = 11
        private const val REQUEST_CAMERA_PERMISSION = 22


        fun newInstance() = FillDataFragment()
    }

    override var presenter: IFillDataPresenter = FillDataPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_fill_data, container, false)

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }

    private lateinit var filePicker: MediaFilePicker
    private val photoAdapter = DelegationAdapter<Any>()
    private val extraPhotos = ArrayList<File>()
    private val photoTpes = arrayListOf<PHOTO_TYPE>(PHOTO_TYPE.PHOTO_TYPE_1, PHOTO_TYPE.PHOTO_TYPE_2, PHOTO_TYPE.PHOTO_TYPE_3, PHOTO_TYPE.PHOTO_TYPE_4)

    private val mainEntryTypeAdapter = DelegationAdapter<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filePicker = MediaFilePicker.Builder().activity(activity)
                .fragment(this)
                .listener(this)
                .activity(activity)
                .savedState(savedInstanceState)
                .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        ivAddPhoto.setOnClickListener {
            dialogFilePicker()
        }

        photoAdapter.manager?.addDelegate(PhotoDelegate({ photo ->
            photoAdapter.remove(photoAdapter.items.indexOf(photo))
            extraPhotos.remove(photo)

            if (extraPhotos.isEmpty()) {
                etFillPhotoHint.visibility = View.VISIBLE
            }

            if (extraPhotos.size < 20) {
                ivAddPhoto.visibility = View.VISIBLE
            }
        }))

        mainEntryTypeAdapter.manager?.addDelegate(MainEntryTaskDelegate({
            dialogFilePicker()
        }))

        mainEntryTypeAdapter.manager?.addDelegate(MainEntryTypeDelegate())
        mainEntryTypeAdapter.manager?.addDelegate(PhotoTypeDelegate())

        rvPhotos.layoutManager = GridLayoutManager(context, 4)
        rvPhotos.adapter = photoAdapter

        rvTasks.layoutManager = LinearLayoutManager(context)
        rvTasks.adapter = mainEntryTypeAdapter

        val list = arrayListOf<Any>()
        choiceTypes.forEach { entryType ->
            list.add(entryType)

            photoTpes.forEach { photoType ->
                entryType.photosId.filter { photos[it].type == photoType }.let {
                    if (it.isNotEmpty()) {
                        list.add(photoType)
                        list.addAll(it.map { photos[it] })
                    }
                }
            }
        }

        mainEntryTypeAdapter.addAll(list)
        rvTasks.isNestedScrollingEnabled = false
    }

    override fun onFilePicked(file: File?) {
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

    private fun dialogFilePicker() {
        val choose = resources.getStringArray(R.array.choose_image_dialog)

        AlertDialog.Builder(activity)
                .setTitle(getString(R.string.profile_edit_dialog_title_profile_pick_photo))
                .setItems(choose) { dialog, which ->
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestStoragePermission(REQUEST_STORAGE_PERMISSION)
                        } else {
                            filePicker.requestGalleryIntent()
                        }
                    } else if (which == 1) {
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

