package services.mobiledev.ru.cheap.ui.main.comments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.text.SpannableString
import android.text.Spannable
import android.text.TextPaint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.CheckBox
import com.example.literalnon.autoreequipment.EntryType
import com.example.literalnon.autoreequipment.R
import com.example.literalnon.autoreequipment.allEntryType
import com.example.literalnon.autoreequipment.data.Extras
import com.example.literalnon.autoreequipment.fillData.FillDataItem
import kotlinx.android.synthetic.main.fragment_add_entry.*
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.AddEntryPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IAddEntryPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IAddEntryView
import java.io.File


class AddEntryFragment : Fragment(), IAddEntryView {

    companion object {
        fun newInstance() = AddEntryFragment()

        //var choiceTypes = arrayListOf<EntryType>()
        //var extras: Extras? = null
    }

    override var presenter: IAddEntryPresenter = AddEntryPresenter()

    private val choiceTypes = arrayListOf<EntryType>()
    //var extras: Extras? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_add_entry, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        //choiceTypes.clear()

        val checkBoxMap = hashMapOf<CheckBox, EntryType>(
                Pair(chbInstallGbo, allEntryType[1]),
                Pair(chbInstallFarcop, allEntryType[2]),
                Pair(chbInstallDvig, allEntryType[3]),
                Pair(chbInstallRefresh, allEntryType[4]),
                Pair(chbInstallStrongBamp, allEntryType[5]),
                Pair(chbOther, allEntryType[6])
        )

        checkBoxMap.forEach { (view, type) ->
            view.isChecked = choiceTypes.contains(type)
            view.text = type.title
            view.setOnClickListener {
                if (!choiceTypes.contains(type)) {
                    choiceTypes.add(type)
                } else {
                    choiceTypes.remove(type)
                }
            }
        }

        btnNext.setOnClickListener {
            if (choiceTypes.isNotEmpty()) {
                presenter.next(FillDataItem("", "", choiceTypes, null))
            }
        }
    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }
}

