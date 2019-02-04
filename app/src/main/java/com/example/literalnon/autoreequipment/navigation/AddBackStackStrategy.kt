package services.mobiledev.ru.cheap.navigation

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import java.util.*

/**
 * Created by dmitry on 20.11.17.
 */
class AddBackStackStrategy(override val fragmentManager: FragmentManager, override val containerId: Int) : INavigationStrategy {

    private val tags: Stack<IBaseItem?> = Stack()

    override fun backStackSize(): Int {
        return tags.size
    }

    override fun updateUi(enumObject: IBaseItem?) {

    }

    override fun updateChildUi(enumObject: IBaseItem?, data: Any?) {

    }

    override fun popFragment() {
        if (!tags.empty()) {
            tags.pop()
            fragmentManager.popBackStack()
        }
    }

    override fun showFragment(enumObject: IBaseItem, data: Any?) {
        showFragmentWithParcelable(enumObject, enumObject.getFragment(), data)
    }

    override fun getCurrentScreen(): IBaseItem? {
        return if (tags.empty()) {
            null
        } else {
            tags.peek()
        }
    }

    override fun showFirstFragment(enumObject: IBaseItem, data: Any?) {
        tags.push(enumObject)

        enumObject.putAnimation(fragmentManager)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(containerId, enumObject.getFragment(), enumObject.getTag())
                .commit()
    }

    override fun showFragmentWithParcelable(enumObject: IBaseItem, fragment: Fragment, data: Any?) {

        /*if (enumObject.getPreviousEnumObject() == null) {

        } else if (tags.contains(enumObject)) {
            while (!tags.empty() && tags.peek() != enumObject) {
                popFragment()
            }
            if (tags.peek() == enumObject) {
                popFragment()
            }
        } *//*else {*//*
            tags.push(enumObject)

            enumObject.putAnimation(fragmentManager)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(containerId, fragment, enumObject.getTag())
                    .addToBackStack(enumObject.getTag())
                    .commit()
        //}*/
        if (enumObject.getPreviousEnumObject() == null) {
            clear()
        } else if (tags.contains(enumObject)) {
            while (!tags.empty() && tags.peek() != enumObject) {
                tags.pop()
                fragmentManager.popBackStack()
            }
        } //else {
            tags.push(enumObject)

            enumObject.putAnimation(fragmentManager)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(containerId, fragment, enumObject.getTag())
                    .addToBackStack(enumObject.getTag())
                    .commitAllowingStateLoss()
        //}
    }

    override fun getCurrentFragment(): Fragment {
        return fragmentManager.findFragmentByTag(getCurrentScreen()?.getTag()) ?: Fragment()
    }

    override fun clear() {
        tags.clear()

        for (i: Int in 0..backStackSize()) {
            fragmentManager.popBackStack()
        }
    }

    override fun backNavigation(): Boolean {
        when {
            getCurrentScreen()?.getPreviousEnumObject() == null -> {
                return false
            }
            tags.empty() -> {
                return false
            }
            tags.size == 1 -> {
                popFragment()
                return false
            }
            else -> {

                val currentEnumObject: IBaseItem? = tags.peek()

                if (currentEnumObject?.getPreviousEnumObject() == null) {
                    clear()
                    return false
                } else {
                    popFragment()
                }
                //while (!tags.empty() && tags.peek()?.getTag() != currentEnumObject?.getPreviousEnumObject()?.getTag()) {

                //}

                if (!tags.empty())
                    updateUi(tags.peek())

                return true
            }
        }
    }
}