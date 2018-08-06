package services.mobiledev.ru.cheap.ui.main.comments.mvp

import com.example.bloold.hackage.mvp.IPresenter
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import services.mobiledev.ru.cheap.navigation.Navigator

/**
 * Created by dmitry on 04.05.18.
 */
interface IEnterNamePresenter : IPresenter<IEnterNameView, IEnterNameModel> {
    fun next()
}

class EnterNamePresenter : IEnterNamePresenter {

    override var view: IEnterNameView? = null

    override var model: IEnterNameModel = EnterNameModel()

    override fun next() {
        getNavigator()?.pushFragment(NavigationMainItems.FILL_ENTRY_SCREEN)
    }

    override fun attachView(view: IEnterNameView) {
        this.view = view
    }

    override fun detachView(view: IEnterNameView) {
        if (this.view?.equals(view) == true) {
            this.view = null
        }
    }

    override fun getNavigator(): Navigator? {
        return view?.getNavigationParent()?.navigator
    }

}