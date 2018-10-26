package com.example.literalnon.autoreequipment

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
import com.example.literalnon.autoreequipment.network.DataService
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import com.google.gson.Gson
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.navigation.AddBackStackStrategy
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.navigation.Navigator
import services.mobiledev.ru.cheap.navigation.ReplaceStrategy
import java.io.File
import java.math.BigInteger

class RegistrationActivity : AppCompatActivity(), INavigationParent {

    override var navigator: Navigator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (LoginController.user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        Fabric.with(this, Crashlytics())
        Realm.init(this)

        navigator = Navigator.Builder()
                .firstFragment(NavigationMainItems.PARTNER_SETTINGS_SCREEN)
                .strategy(ReplaceStrategy(supportFragmentManager, R.id.container))
                .build()

        getData()

        bottomNavigationView.visibility = View.GONE
    }

    private fun getData() {
        navigator?.openFirstFragment()
        progressBar.visibility = View.GONE
    }
}