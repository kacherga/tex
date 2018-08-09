package com.example.bloold.hackage.mvp

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import services.mobiledev.ru.cheap.navigation.INavigationParent
import services.mobiledev.ru.cheap.navigation.Navigator

/**
 * Created by bloold on 01.04.18.
 */
interface IView<Presenter> {
    var presenter: Presenter

    fun getNavigationParent(): INavigationParent

    fun showLoading() {}
    fun dismissLoading() {}
}

interface IPresenter<View, Model> {
    var view: View?
    var model: Model

    fun attachView(view: View)
    fun detachView(view: View)

    fun getNavigator(): Navigator?
}

interface IModel {}