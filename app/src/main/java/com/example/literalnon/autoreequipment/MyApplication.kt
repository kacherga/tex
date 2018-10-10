package com.example.literalnon.autoreequipment

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import services.mobiledev.ru.cheap.data.Prefs
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(config)
        Prefs.init(this)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build())

    }
}