package com.example.literalnon.autoreequipment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import services.mobiledev.ru.cheap.navigation.AddBackStackStrategy
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.navigation.Navigator

class MainActivity : AppCompatActivity(), INavigationParent {

    override var navigator: Navigator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)

        navigator = Navigator.Builder()
                .firstFragment(NavigationMainItems.ADD_ENTRY_SCREEN)
                .strategy(AddBackStackStrategy(supportFragmentManager, R.id.container))
                .build()

        navigator?.openFirstFragment()

        ivActiveEntries.setOnClickListener {
            navigator?.pushFragment(NavigationMainItems.LIST_ACTIVE_ENTRY_SCREEN)
        }

        ivLogin.setOnClickListener {
            navigator?.pushFragment(NavigationMainItems.ENTER_NAME_SCREEN)
        }

        ivAddEntry.setOnClickListener {
            navigator?.pushFragment(NavigationMainItems.ADD_ENTRY_SCREEN)
        }
    }

    override fun onBackPressed() {
        if (navigator?.backNavigation() != true) {
            super.onBackPressed()
        }
    }

}

@GlideModule
class SomeAppGlideModule : AppGlideModule() {}