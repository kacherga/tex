package services.mobiledev.ru.cheap.ui.main.comments.mvp

import com.example.bloold.hackage.mvp.IView

/**
 * Created by dmitry on 04.05.18.
 */

interface IAddPartnerView : IView<IAddPartnerPresenter> {
    fun partnerAddSuccess()
    fun partnerAddFailed()
    fun partnerNotFound()
}
