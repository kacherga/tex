package services.mobiledev.ru.cheap.navigation

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

/**
 * Created by dmitry on 22.11.17.
 */
interface IBaseItem {
    var data: Any?

    fun getTag(): String
    fun getPreviousEnumObject(): IBaseItem?
    fun getFragment(): Fragment

    fun putAnimation(fragmentManager: FragmentManager): FragmentTransaction = fragmentManager.beginTransaction()
}