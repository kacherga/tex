package com.example.literalnon.autoreequipment.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface RegistrationService {

    @GET("app/partners.txt")
    fun registration(): Observable<String>
}