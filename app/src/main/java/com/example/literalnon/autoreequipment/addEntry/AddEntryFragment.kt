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
import com.example.literalnon.autoreequipment.EntryType
import com.example.literalnon.autoreequipment.MainEntryType
import com.example.literalnon.autoreequipment.R
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
        private val choiceTypes = arrayListOf<EntryType>()
    }

    override var presenter: IAddEntryPresenter = AddEntryPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_add_entry, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chbInstallGbo.isChecked = choiceTypes.contains(MainEntryType.TYPE_1)
        chbInstallFarcop.isChecked = choiceTypes.contains(MainEntryType.TYPE_2)
        chbInstallDvig.isChecked = choiceTypes.contains(MainEntryType.TYPE_3)
        chbInstallRefresh.isChecked = choiceTypes.contains(MainEntryType.TYPE_4)
        chbInstallStrongBamp.isChecked = choiceTypes.contains(MainEntryType.TYPE_5)
        chbOther.isChecked = choiceTypes.contains(MainEntryType.TYPE_6)

        chbInstallGbo.setOnClickListener {
            choiceTypes.add(MainEntryType.TYPE_1)
        }

        chbInstallFarcop.setOnClickListener {
            choiceTypes.add(MainEntryType.TYPE_2)
        }

        chbInstallDvig.setOnClickListener {
            choiceTypes.add(MainEntryType.TYPE_3)
        }

        chbInstallRefresh.setOnClickListener {
            choiceTypes.add(MainEntryType.TYPE_4)
        }

        chbInstallStrongBamp.setOnClickListener {
            choiceTypes.add(MainEntryType.TYPE_5)
        }

        chbOther.setOnClickListener {
            choiceTypes.add(MainEntryType.TYPE_6)
        }

        btnNext.setOnClickListener {
            presenter.next()
        }
    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }
}

