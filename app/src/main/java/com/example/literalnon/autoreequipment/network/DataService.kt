package com.example.literalnon.autoreequipment.network

import com.example.literalnon.autoreequipment.EntryType
import com.example.literalnon.autoreequipment.Photo
import com.example.literalnon.autoreequipment.PhotoType
import io.reactivex.Observable
import retrofit2.http.GET

interface DataService{

    @GET("/app/entryTypes.rtf")
    fun getEntryTypes(): Observable<List<EntryType>>

    @GET("/app/photoTypes.rtf")
    fun getPhotoTypes(): Observable<List<PhotoType>>

    @GET("/app/photos.rtf")
    fun getPhotos(): Observable<List<Photo>>
}