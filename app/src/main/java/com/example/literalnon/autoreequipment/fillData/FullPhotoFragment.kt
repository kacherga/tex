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
import android.util.Log
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
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
import kotlinx.android.synthetic.main.fragment_full_photo.*
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.FillDataPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IFillDataPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IFillDataView
import java.io.File


class FullPhotoFragment : Fragment() {

    companion object {
        private const val EXTRA_URL = "url"

        fun newInstance(url: String): FullPhotoFragment {
            return FullPhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_URL, url)
                }
            }
        }
    }

    private lateinit var photo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments?.containsKey(EXTRA_URL) == true) {
            photo = arguments?.getString(EXTRA_URL) ?: ""
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_full_photo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivImage.setImage(ImageSource.uri(photo))
        /*Glide.with(ivImage)
                .load(photo)
                .into(ivImage)*/
    }
}

