package services.mobiledev.ru.cheap.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Created by dmitry on 07.05.18.
 */
object Prefs {

    private val gson = Gson()

    private var settings: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    @SuppressLint("CommitPrefEdits")
    fun init(context: Context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        editor = settings!!.edit()
    }

    fun get(): SharedPreferences {
        return settings!!
    }

    fun edit(): SharedPreferences.Editor {
        return editor!!
    }

    fun clean() {
        editor!!.clear().apply()
    }

    fun <T> save(key: String, model: T) {
        edit().putString(key, gson.toJson(model, object : TypeToken<T>() {}.type)).apply()
    }

    fun <T> load(key: String, klass: Class<T>): T? {
        val json = get().getString(key, null) ?: return null
        return gson.fromJson<T>(json, klass)
    }

    fun <T> load(key: String, type: Type): T? {
        val json = get().getString(key, null) ?: return null
        return gson.fromJson<T>(json, type)
    }

    fun remove(vararg keys: String) {
        for (key in keys) {
            editor!!.remove(key)
        }

        editor!!.apply()
    }
}