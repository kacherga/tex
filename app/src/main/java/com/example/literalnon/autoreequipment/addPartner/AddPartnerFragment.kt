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
import com.example.literalnon.autoreequipment.data.Entry
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_add_partner.*
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.AddPartnerPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IAddPartnerPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IAddPartnerView
import java.io.File


class AddPartnerFragment : Fragment(), IAddPartnerView {

    companion object {
        fun newInstance() = AddPartnerFragment()
    }

    override var presenter: IAddPartnerPresenter = AddPartnerPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_add_partner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        btnNext.setOnClickListener {
            presenter.next(etName.text.toString())
        }
    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }
}

