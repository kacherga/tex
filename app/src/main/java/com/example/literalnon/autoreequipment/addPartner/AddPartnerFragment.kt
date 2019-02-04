package services.mobiledev.ru.cheap.ui.main.comments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
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
import com.example.literalnon.autoreequipment.MainActivity
import com.example.literalnon.autoreequipment.RegistrationActivity
import com.example.literalnon.autoreequipment.data.Entry
import com.example.literalnon.autoreequipment.utils.NavigationMainItems
import io.realm.Realm


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
            presenter.next(etPhone.rawText)
            //Toast.makeText(context, getString(R.string.fragment_login_toast), Toast.LENGTH_SHORT).show()
        }

        tvName.text = LoginController.user?.name ?: ""
        tvPhone.text = LoginController.user?.phone ?: ""

        etPhone.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnNext.performClick()
            }
            false
        }

        /*etName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {

            }
            false
        }*/

    }

    private val handler = Handler()
    private var runnable: Runnable = Runnable {  }
    private val DELAYED = 1000L

    override fun partnerAddSuccess() {
        alertDialog?.cancel()

        runnable = Runnable {
            context?.startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
        }

        alertDialog = AlertDialog.Builder(context!!)
                .setTitle(R.string.fragment_login_alert_title)
                .setMessage(getString(R.string.fragment_login_success, LoginController.user?.name))
                .setOnDismissListener {
                    if (activity is RegistrationActivity) {
                        context?.startActivity(Intent(context, MainActivity::class.java))
                        activity?.finish()
                    } else {
                        getNavigationParent().navigator?.pushFragment(NavigationMainItems.ADD_ENTRY_SCREEN)
                    }
                }
                .create()

        alertDialog?.show()

        handler.postDelayed(runnable, DELAYED)

        tvName.text = LoginController.user?.name ?: ""
        tvPhone.text = LoginController.user?.phone ?: ""

        val realm = Realm.getDefaultInstance()

        realm?.beginTransaction()

        realm?.where(Entry::class.java)?.findAll()?.deleteAllFromRealm()

        realm?.commitTransaction()

        /*realm?.executeTransaction({ bgRealm ->

        })*/

        realm.close()
    }

    private var alertDialog: AlertDialog? = null

    override fun showLoading() {
        alertDialog?.cancel()

        alertDialog = AlertDialog.Builder(context!!)
                .setTitle(R.string.fragment_login_alert_title)
                .setMessage(R.string.fragment_login_load)
                .create()

        alertDialog?.show()

    }

    override fun dismissLoading() {
        alertDialog?.cancel()
        alertDialog = null
    }

    override fun partnerAddFailed() {
        alertDialog?.cancel()

        alertDialog = AlertDialog.Builder(context!!)
                .setTitle(R.string.fragment_login_alert_title)
                .setMessage(R.string.fragment_login_failed)
                .create()

        alertDialog?.show()

        tvName.text = LoginController.user?.name ?: ""
        tvPhone.text = LoginController.user?.phone ?: ""
    }

    override fun partnerNotFound() {
        alertDialog?.cancel()

        alertDialog = AlertDialog.Builder(context!!)
                .setTitle(R.string.fragment_login_alert_title)
                .setMessage(getString(R.string.fragment_login_not_found, etPhone.rawText))
                .create()

        alertDialog?.show()

        tvName.text = LoginController.user?.name ?: ""
        tvPhone.text = LoginController.user?.phone ?: ""
    }

    override fun getNavigationParent(): INavigationParent {
        return activity as INavigationParent
    }

    override fun onDestroyView() {
        handler.removeCallbacks(runnable)
        super.onDestroyView()
    }
}

