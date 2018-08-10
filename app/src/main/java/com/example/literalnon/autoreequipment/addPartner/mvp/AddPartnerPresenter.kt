package services.mobiledev.ru.cheap.ui.main.comments.mvp

import com.example.bloold.hackage.mvp.IPresenter
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.data.User
import services.mobiledev.ru.cheap.navigation.Navigator

/**
 * Created by dmitry on 04.05.18.
 */
interface IAddPartnerPresenter : IPresenter<IAddPartnerView, IAddPartnerModel> {
    fun next(user: User)
}

class AddPartnerPresenter : IAddPartnerPresenter {

    override var view: IAddPartnerView? = null

    override var model: IAddPartnerModel = AddPartnerModel()

    override fun next(user: User) {
        LoginController.user = user
    }

    override fun attachView(view: IAddPartnerView) {
        this.view = view
    }

    override fun detachView(view: IAddPartnerView) {
        if (this.view?.equals(view) == true) {
            this.view = null
        }
    }

    override fun getNavigator(): Navigator? {
        return view?.getNavigationParent()?.navigator
    }

}