package services.mobiledev.ru.cheap.ui.main.comments.mvp

import com.example.bloold.hackage.mvp.IPresenter
import com.example.literalnon.autoreequipment.EntryType
import com.example.literalnon.autoreequipment.data.Entry
import com.example.literalnon.autoreequipment.data.EntryObject
import com.example.literalnon.autoreequipment.data.Extras
import com.example.literalnon.autoreequipment.fillData.FillDataItem
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import com.google.gson.Gson
import services.mobiledev.ru.cheap.navigation.Navigator

/**
 * Created by dmitry on 04.05.18.
 */
interface IActiveEntryPresenter : IPresenter<IActiveEntryView, IActiveEntryModel> {
    fun next(name: String, phone: String, choiceType: ArrayList<EntryType>, extras: Extras?)
    //fun openEdit(name: String, phone: String, choiceType: ArrayList<EntryType>, extras: Extras?)
    fun openEdit(entry: EntryObject)
}

class ActiveEntryPresenter : IActiveEntryPresenter {

    override var view: IActiveEntryView? = null

    override var model: IActiveEntryModel = ActiveEntryModel()

    override fun next(name: String, phone: String, choiceType: ArrayList<EntryType>, extras: Extras?) {
        NavigationMainItems.FILL_ENTRY_SCREEN.data = FillDataItem(name, phone, choiceType, extras)
        getNavigator()?.pushFragment(NavigationMainItems.FILL_ENTRY_SCREEN)
    }

    override fun openEdit(entry: EntryObject) {
        NavigationMainItems.EDIT_ENTRY_SCREEN.data = FillDataItem(
                entry.name ?: "",
                entry.phone ?: "",
                arrayListOf(),
                null,
                true
        )

        getNavigator()?.pushFragment(NavigationMainItems.EDIT_ENTRY_SCREEN)
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