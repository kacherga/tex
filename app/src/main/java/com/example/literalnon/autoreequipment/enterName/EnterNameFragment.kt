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
import kotlinx.android.synthetic.main.fragment_enter_name.*
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.EnterNamePresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IEnterNamePresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IEnterNameView
import java.io.File


class EnterNameFragment : Fragment(), IEnterNameView {

    companion object {
        fun newInstance() = EnterNameFragment()
    }

    override var presenter: IEnterNamePresenter = EnterNamePresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_enter_name, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnNext.setOnClickListener {
            if (etName.text.toString().isNotEmpty()) {
                presenter.next()
            }
        }
    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }
}

