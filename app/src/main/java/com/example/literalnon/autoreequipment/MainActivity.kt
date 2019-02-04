package com.example.literalnon.autoreequipment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.bottomappbar.BottomAppBar
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import com.betcityru.dyadichko_da.betcityru.ui.createService
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.crashlytics.android.Crashlytics
import com.example.literalnon.autoreequipment.activeEntry.ActiveEntryFragment
import com.example.literalnon.autoreequipment.data.EntryObject
import com.example.literalnon.autoreequipment.network.DataService
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import com.google.gson.Gson
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import services.mobiledev.ru.cheap.navigation.AddBackStackStrategy
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.navigation.Navigator
import services.mobiledev.ru.cheap.navigation.ReplaceStrategy
import java.io.File
import java.math.BigInteger

class MainActivity : AppCompatActivity(), INavigationParent {

    override var navigator: Navigator? = null

    companion object {
        var context: MainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        context = this

        Fabric.with(this, Crashlytics())
        Realm.init(this)

        navigator = Navigator.Builder()
                .firstFragment(NavigationMainItems.ADD_ENTRY_SCREEN)
                .strategy(AddBackStackStrategy(supportFragmentManager, R.id.container))
                .build()

        //getData()

        navigator?.openFirstFragment()
        progressBar.visibility = View.GONE
        /*ivActiveEntries.setOnClickListener {
            navigator?.pushFragment(NavigationMainItems.LIST_ACTIVE_ENTRY_SCREEN)
        }


        ivAddEntry.setOnClickListener {
            navigator?.pushFragment(NavigationMainItems.ADD_ENTRY_SCREEN)
        }*/
        //setSupportActionBar(bottomNavigationView)

        //bottomNavigationView.replaceMenu(R.menu.menu_bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.bottomNavigationAddNew

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when {
                item?.itemId == R.id.bottomNavigationActiveList -> {
                    //NavigationMainItems.LIST_ACTIVE_ENTRY_SCREEN.data = true
                    navigator?.pushFragment(NavigationMainItems.LIST_ACTIVE_ENTRY_SCREEN)
                    true
                }
                item?.itemId == R.id.bottomNavigationAddNew -> {
                    navigator?.pushFragment(NavigationMainItems.ADD_ENTRY_SCREEN)
                    true
                }
                item?.itemId == R.id.bottomNavigationArchive -> {
                    //NavigationMainItems.LIST_ACTIVE_ENTRY_SCREEN.data = false
                    navigator?.pushFragment(NavigationMainItems.LIST_ARCHIVE_ENTRY_SCREEN)
                    true
                }
                else -> {
                    true
                }
            }
        }


    }

    private fun getData() {

        val service = createService(DataService::class.java)

        progressBar.visibility = View.VISIBLE

        service.getEntryTypes()
                .flatMap {
                    allEntryType = ArrayList<EntryType>().apply {
                        addAll(it)
                    }
                    service.getPhotoTypes()
                }.flatMap {
                    allPhotoTypes = ArrayList<PhotoType>().apply {
                        addAll(it)
                    }
                    service.getPhotos()
                }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->

                    photos = ArrayList<Photo>().apply {
                        addAll(list)
                    }

                    navigator?.openFirstFragment()
                    progressBar.visibility = View.GONE
                }, {
                    Toast.makeText(this, getString(R.string.send_file_failed), Toast.LENGTH_SHORT).show()
                    navigator?.openFirstFragment()
                    progressBar.visibility = View.GONE
                    it.printStackTrace()
                })

        progressBar.visibility = View.GONE
        navigator?.openFirstFragment()

    }

    fun sendNotificate(listEntry: ArrayList<EntryObject>) {
        if (navigator?.getCurrentScreen() == NavigationMainItems.LIST_ACTIVE_ENTRY_SCREEN ||
                navigator?.getCurrentScreen() == NavigationMainItems.LIST_ARCHIVE_ENTRY_SCREEN) {
            (navigator?.getCurrentScreen()?.getFragment() as ActiveEntryFragment).update(listEntry)
        }
    }

    override fun onBackPressed() {
        if (navigator?.backNavigation() != true) {
            //super.onBackPressed()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.navigation_settings) {
            navigator?.pushFragment(NavigationMainItems.PARTNER_SETTINGS_SCREEN)
            //startActivity(Intent(this, RegistrationActivity::class.java))
            //finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        context = null
        super.onDestroy()
    }
}

@GlideModule
class SomeAppGlideModule : AppGlideModule() {}