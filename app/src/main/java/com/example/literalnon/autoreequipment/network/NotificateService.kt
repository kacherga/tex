package com.example.literalnon.autoreequipment.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificateService {

    @GET("docs/sync.php")
    fun notificate(@Query("city") city: String,
                   @Query("partner") partner: String,
                   @Query("name") name: String,
                   @Query("type") type: String): Observable<ResponseBody>
}