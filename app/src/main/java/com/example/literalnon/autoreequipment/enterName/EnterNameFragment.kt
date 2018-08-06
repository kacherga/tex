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
import kotlinx.android.synthetic.main.fragment_enter_name.*
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.EnterNamePresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IEnterNamePresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IEnterNameView
import java.io.File


class EnterNameFragment : Fragment(), IEnterNameView {

    companion object {
        private val REQUEST_VOICE_SEARCH = 7823

        fun newInstance() = EnterNameFragment()
    }

    override var presenter: IEnterNamePresenter = EnterNamePresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_enter_name, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        btnNext.setOnClickListener {
            if (etName.text.toString().isNotEmpty()) {
                presenter.next()
            }
        }

        ivVoiceSearch.setOnClickListener {
            displaySpeechRecognizer()
        }
    }

    private fun displaySpeechRecognizer() {
        val intentS = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intentS.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intentS, REQUEST_VOICE_SEARCH)
    }

    override fun onActivityResult(requestCode: Int, resCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VOICE_SEARCH && resCode == Activity.RESULT_OK) {
            val result = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val fio = result[0].toString().split(" ")
            etName.setText(fio.fold("", { acc, s ->
                "$acc${s.first().toUpperCase()}${s.substring(1, s.length)}\n"
            }))

        }
        super.onActivityResult(requestCode, resCode, data)
    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }
}

