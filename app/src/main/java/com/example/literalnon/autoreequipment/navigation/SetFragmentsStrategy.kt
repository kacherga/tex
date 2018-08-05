package services.mobiledev.ru.cheap.navigation

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

/**
 * Created by dmitry on 20.11.17.
 */
abstract class SetFragmentsStrategy(override val fragmentManager: FragmentManager, override val containerId: Int) : INavigationStrategy {

    private val tags: HashSet<IBaseItem?> = HashSet<IBaseItem?>()
    private var currentItem: IBaseItem? = null

    override fun popFragment() {
        fragmentManager.popBackStack()
    }

    override fun showFragment(enumObject: IBaseItem, data: Any?) {
        showFragmentWithParcelable(enumObject, enumObject.getFragment(), data)
    }

    override fun showFragmentWithParcelable(enumObject: IBaseItem, fragment: Fragment, data: Any?) {
        //super.showFragment(enumObject, data)

        /*if (enumObject.getPreviousEnumObject() != null) {
            enumObject.putAnimation(fragmentManager)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .add(containerId, fragment, enumObject.getTag())
                    .addToBackStack(enumObject.getTag())
                    .commit()
        }*/
        if (tags.contains(enumObject)) {
            enumObject.putAnimation(fragmentManager)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .hide(fragmentManager.findFragmentByTag(currentItem?.getTag()))
                    .show(fragmentManager.findFragmentByTag(enumObject.getTag()))
                    .commit()
        } else {
            tags.add(enumObject)

            enumObject.putAnimation(fragmentManager)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .add(containerId, fragment, enumObject.getTag())
                    .hide(fragmentManager.findFragmentByTag(currentItem?.getTag()))
                    .commit()
        }

        currentItem = enumObject
    }

    override fun getCurrentScreen(): IBaseItem? {
        return currentItem
    }

    override fun getCurrentFragment(): Fragment {
        return fragmentManager.findFragmentByTag(getCurrentScreen()?.getTag())
    }

    override fun clear() {
        tags.clear()

        for (i: Int in 0..backStackSize()) {
            fragmentManager.popBackStack()
        }
    }

    override fun showFirstFragment(enumObject: IBaseItem, data: Any?) {
        currentItem = enumObject

        tags.add(currentItem)

        fragmentManager.beginTransaction()
                .add(containerId, enumObject.getFragment(), enumObject.getTag())
                .commit()
    }

    override fun backNavigation(): Boolean {

        return when {
            tags.isEmpty() ->
                false
            currentItem?.getPreviousEnumObject() == null ->
                true
            currentItem?.getPreviousEnumObject() != null -> {
                currentItem = currentItem?.getPreviousEnumObject()

                tags.remove(currentItem)

                currentItem!!.putAnimation(fragmentManager)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .show(fragmentManager.findFragmentByTag(currentItem?.getTag()))
                        .commit()

                true
            }
            else ->
                false
        }
    }
}