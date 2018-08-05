package services.mobiledev.ru.cheap.navigation

import android.content.Context
import android.support.v4.app.Fragment

/**
 * Created by bloold on 12.04.18.
 */
abstract class BaseChildFragment : Fragment(), ChildNavigationView {

    var parent: SimpleParent? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            parent = context as SimpleParent
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

interface SimpleParent {
    fun action(item: IBaseItem?, data: Any? = null)
}

interface SimpleChild {
    fun onBackPressed(): Boolean
}