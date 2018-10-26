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
    fun next(phone: String)
}

class AddPartnerPresenter : IAddPartnerPresenter {

    override var view: IAddPartnerView? = null

    override var model: IAddPartnerModel = AddPartnerModel()

    override fun next(phone: String) {
        view?.showLoading()
        LoginController.user = null

        model?.registration(phone)
                //?.doAfterTerminate { view?.dismissLoading() }
                .subscribe({
                    if (it.first) {
                        LoginController.user = it.second
                        view?.partnerAddSuccess()
                    } else {
                        view?.partnerNotFound()
                    }
                }, {
                    it.printStackTrace()
                    view?.partnerAddFailed()
                })

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