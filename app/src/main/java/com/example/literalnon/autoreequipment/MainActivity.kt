package com.example.literalnon.autoreequipment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.crashlytics.android.Crashlytics
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import services.mobiledev.ru.cheap.navigation.AddBackStackStrategy
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.navigation.Navigator
import services.mobiledev.ru.cheap.navigation.ReplaceStrategy
import java.math.BigInteger

class MainActivity : AppCompatActivity(), INavigationParent {

    override var navigator: Navigator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        Fabric.with(this, Crashlytics())
        Realm.init(this)

        navigator = Navigator.Builder()
                .firstFragment(NavigationMainItems.ADD_ENTRY_SCREEN)
                .strategy(ReplaceStrategy(supportFragmentManager, R.id.container))
                .build()

        navigator?.openFirstFragment()

        ivActiveEntries.setOnClickListener {
            navigator?.pushFragment(NavigationMainItems.LIST_ACTIVE_ENTRY_SCREEN)
        }


        ivAddEntry.setOnClickListener {
            navigator?.pushFragment(NavigationMainItems.ADD_ENTRY_SCREEN)
        }

        var n = BigInteger.ONE
        var fact = BigInteger.ONE

        while (!fact.toString().contains("2013")) {
            ++n
            fact *= n
        }

        Log.e("tag", "n: ${n} : ${fact}")
    }

    override fun onBackPressed() {
        if (navigator?.backNavigation() != true) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.navigation_settings) {
            navigator?.pushFragment(NavigationMainItems.PARTNER_SETTINGS_SCREEN)
        }

        return super.onOptionsItemSelected(item)
    }

}

@GlideModule
class SomeAppGlideModule : AppGlideModule() {}