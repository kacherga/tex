package com.betcityru.dyadichko_da.betcityru.ui

import com.example.literalnon.autoreequipment.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private var BASE_URL = "http://app.tex-expert.ru/docs/" //TODO будет генерация url, возможно динамический url!!

private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)

private val gson = GsonBuilder().create()

private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient.build())

private val retrofit = builder.build()

fun <S> createService(
        serviceClass: Class<S>): S {
    return retrofit.create(serviceClass)
}

fun changeBaseUrl(url: String) {
    BASE_URL = url
}