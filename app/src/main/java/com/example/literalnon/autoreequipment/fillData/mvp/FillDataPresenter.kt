package services.mobiledev.ru.cheap.ui.main.comments.mvp

import com.example.bloold.hackage.mvp.IPresenter
import com.example.literalnon.autoreequipment.Photo
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import services.mobiledev.ru.cheap.navigation.Navigator

/**
 * Created by dmitry on 04.05.18.
 */
interface IFillDataPresenter : IPresenter<IFillDataView, IFillDataModel> {
    fun next()
    fun openPhoto(photo: Photo)
}

class FillDataPresenter : IFillDataPresenter {

    override var view: IFillDataView? = null

    override var model: IFillDataModel = FillDataModel()

    override fun next() {
        getNavigator()?.pushFragment(NavigationMainItems.ENTER_NAME_SCREEN)
    }

    override fun openPhoto(photo: Photo) {
        NavigationMainItems.FULL_PHOTO_SCREEN.data = photo.photos?.first()
        getNavigator()?.pushFragment(NavigationMainItems.FULL_PHOTO_SCREEN)
    }

    override fun attachView(view: IFillDataView) {
        this.view = view
    }

    override fun detachView(view: IFillDataView) {
        if (this.view?.equals(view) == true) {
            this.view = null
        }
    }

    override fun getNavigator(): Navigator? {
        return view?.getNavigationParent()?.navigator
    }

}