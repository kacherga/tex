package services.mobiledev.ru.cheap.ui.main.comments

import android.app.Activity
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
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.example.literalnon.autoreequipment.EntryType
import com.example.literalnon.autoreequipment.MainEntryType
import com.example.literalnon.autoreequipment.R
import com.example.literalnon.autoreequipment.activeEntry.ActiveEntryDelegate
import com.example.literalnon.autoreequipment.adapters.DelegationAdapter
import com.example.literalnon.autoreequipment.data.Entry
import com.example.literalnon.autoreequipment.fillData.MainEntryTypeDelegate
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_active_entry.*
import kotlinx.android.synthetic.main.fragment_enter_name.*
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.ActiveEntryPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IActiveEntryPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IActiveEntryView
import java.io.File


class ActiveEntryFragment : Fragment(), IActiveEntryView {

    companion object {
        fun newInstance() = ActiveEntryFragment()
    }

    override var presenter: IActiveEntryPresenter = ActiveEntryPresenter()
    private val adapter = DelegationAdapter<Any>()

    private var realm: Realm? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_active_entry, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        adapter.manager?.addDelegate(ActiveEntryDelegate())
        rvActiveEntry.layoutManager = LinearLayoutManager(context)
        rvActiveEntry.adapter = adapter

        realm = Realm.getDefaultInstance()
        realm?.beginTransaction()

        val entries = realm?.where(Entry::class.java)?.findAll()
        adapter.addAll(entries?.toList())
    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
    }
}

