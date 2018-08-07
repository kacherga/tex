package services.mobiledev.ru.cheap.ui.main.comments.mvp

import com.example.bloold.hackage.mvp.IPresenter
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import services.mobiledev.ru.cheap.navigation.Navigator

/**
 * Created by dmitry on 04.05.18.
 */
interface IActiveEntryPresenter : IPresenter<IActiveEntryView, IActiveEntryModel> {
    fun next()
}

class ActiveEntryPresenter : IActiveEntryPresenter {

    override var view: IActiveEntryView? = null

    override var model: IActiveEntryModel = ActiveEntryModel()

    override fun next() {
        getNavigator()?.pushFragment(NavigationMainItems.FILL_ENTRY_SCREEN)
    }

    override fun attachView(view: IActiveEntryView) {
        this.view = view
    }

    override fun detachView(view: IActiveEntryView) {
        if (this.view?.equals(view) == true) {
            this.view = null
        }
    }

    override fun getNavigator(): Navigator? {
        return view?.getNavigationParent()?.navigator
    }

}