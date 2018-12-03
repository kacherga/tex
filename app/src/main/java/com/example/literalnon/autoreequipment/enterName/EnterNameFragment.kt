package services.mobiledev.ru.cheap.ui.main.comments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.view.inputmethod.EditorInfo
import com.example.literalnon.autoreequipment.R
import kotlinx.android.synthetic.main.fragment_enter_name.*
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.EnterNamePresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IEnterNamePresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IEnterNameView


class EnterNameFragment : Fragment(), IEnterNameView {

    companion object {
        private val REQUEST_VOICE_SEARCH = 7823

        var name = ""
        var phone = ""

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
                name = etName.text.toString()
                phone = etNumber.text.toString()
                presenter.next()
            }
        }

        etNumber.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnNext.performClick()
            }
            false
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
                "$acc${s.first().toUpperCase()}${s.substring(1, s.length)} "
            }).trim())

        }
        super.onActivityResult(requestCode, resCode, data)
    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }
}

