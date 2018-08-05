package services.mobiledev.ru.cheap.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * Created by bloold on 08.04.18.
 */
class Navigator : BaseNavigator {

    class Builder {

        private var navigator: Navigator = Navigator()

        fun build(): Navigator {
            return navigator
        }

        fun strategy(navigationStrategy: INavigationStrategy): Builder {
            navigator.navigationStrategy = navigationStrategy
            return this
        }

        fun firstFragment(firstFragment: IBaseItem): Builder {
            navigator.firstFragment = firstFragment
            return this
        }
    }

    private var navigationStrategy: INavigationStrategy? = null
    private var firstFragment: IBaseItem? = null

    override fun showScreen(enumObject: IBaseItem, data: Any?) {
        pushFragment(enumObject, data)
    }

    override fun pushFragment(enumObject: IBaseItem, data: Any?) {
        navigationStrategy?.showFragment(enumObject, data)
        navigationStrategy?.updateUi(enumObject)
    }

    override fun pushFragmentWithoutUpdate(enumObject: IBaseItem, data: Any?) {
        navigationStrategy?.showFragment(enumObject, data)
    }

    override fun openFirstFragment(data: Any?) {
        if (firstFragment == null) {
            throw RuntimeException("must include first fragment")
        } else {
            navigationStrategy?.showFirstFragment(firstFragment!!, data)
        }
    }

    override fun getCurrentScreen(): IBaseItem? {
        return navigationStrategy?.getCurrentScreen()
    }

    override fun backNavigation(): Boolean {
        return navigationStrategy?.backNavigation() ?: false
    }

    override fun startActivity(activity: Activity, cls: Class<*>, flag: Int?) {
        val intent = Intent(activity, cls)

        if (flag != null) {
            intent.addFlags(flag)
        }
        activity.startActivity(intent)
    }

}