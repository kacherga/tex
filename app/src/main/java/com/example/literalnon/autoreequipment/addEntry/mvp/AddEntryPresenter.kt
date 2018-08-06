package services.mobiledev.ru.cheap.ui.main.comments.mvp

import com.example.bloold.hackage.mvp.IPresenter
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import services.mobiledev.ru.cheap.navigation.Navigator

/**
 * Created by dmitry on 04.05.18.
 */
interface IAddEntryPresenter : IPresenter<IAddEntryView, IAddEntryModel> {
    fun next()
}

class AddEntryPresenter : IAddEntryPresenter {

    override var view: IAddEntryView? = null

    override var model: IAddEntryModel = AddEntryModel()

    override fun next() {
        getNavigator()?.pushFragment(NavigationMainItems.ENTER_NAME_SCREEN)
    }

    override fun attachView(view: IAddEntryView) {
        this.view = view
    }

    override fun detachView(view: IAddEntryView) {
        if (this.view?.equals(view) == true) {
            this.view = null
        }
    }

    override fun getNavigator(): Navigator? {
        return view?.getNavigationParent()?.navigator
    }

}