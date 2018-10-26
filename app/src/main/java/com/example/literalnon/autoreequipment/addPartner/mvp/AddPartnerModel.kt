package services.mobiledev.ru.cheap.ui.main.comments.mvp

import android.util.Log
import com.betcityru.dyadichko_da.betcityru.ui.createService
import com.example.bloold.hackage.mvp.IModel
import com.example.literalnon.autoreequipment.network.RegistrationService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import services.mobiledev.ru.cheap.data.User

/**
 * Created by dmitry on 04.05.18.
 */
interface IAddPartnerModel : IModel {
    fun registration(phone: String): Observable<Pair<Boolean, User?>>
}

class AddPartnerModel : IAddPartnerModel {
    val service = createService(RegistrationService::class.java)

    override fun registration(phone: String): Observable<Pair<Boolean, User?>> {
        return service.registration()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Log.e("mapping", "str : ${it}")
                            val user = it.lines().find {
                                Log.e("registration", "phone : ${phone} : str : ${it}")
                                it.contains(phone)
                            }?.let {
                                val userLine = it.split(";")
                                if (userLine.size > 1) {
                                    User(userLine[1], userLine[0])
                                } else {
                                    null
                                }
                            }
                    Observable.just(Pair(user != null, user))
                }

    }
}
