package services.mobiledev.ru.cheap.ui.main.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.widget.Toast
import com.example.literalnon.autoreequipment.R
import kotlinx.android.synthetic.main.fragment_add_partner.*
import services.mobiledev.ru.cheap.data.LoginController
import services.mobiledev.ru.cheap.data.User
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.ui.main.comments.mvp.AddPartnerPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IAddPartnerPresenter
import services.mobiledev.ru.cheap.ui.main.comments.mvp.IAddPartnerView
import android.view.inputmethod.EditorInfo
import android.widget.TextView




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
            presenter.next(User(etName.text.toString(), etPhone.text.toString()))
            Toast.makeText(context, getString(R.string.fragment_login_toast), Toast.LENGTH_SHORT).show()

            tvName.text = LoginController.user?.name ?: ""
            tvPhone.text = LoginController.user?.phone ?: ""
        }

        tvName.text = LoginController.user?.name ?: ""
        tvPhone.text = LoginController.user?.phone ?: ""

        etPhone.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnNext.performClick()
            }
            false
        }

        etName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {

            }
            false
        }

    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }
}

