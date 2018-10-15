package com.example.literalnon.autoreequipment.data

import io.realm.RealmList
import io.realm.RealmObject

open class Entry : RealmObject() {
    var name: String? = null
    var phone: String? = null
    var workTypes: RealmList<WorkType>? = null
    var sendedAt: Long? = null
}

open class WorkType : RealmObject() {
    var name: String? = null
    var photos: RealmList<RealmPhoto>? = null
    var description: String? = null
    var sendedAt: Long? = null
}

open class RealmPhoto : RealmObject() {
    var id: Int? = null
    var name: String? = null
    var photo: String? = null
    var type: String? = null
    var sendedAt: Long? = null
}

open class EntryObject(
    var name: String? = null,
    var phone: String? = null,
    var workTypes: List<WorkTypeObject>? = null,
    var sendedAt: Long? = null
)

open class WorkTypeObject(
    var name: String? = null,
    var photos: List<PhotoObject>? = null,
    var description: String? = null,
    var sendedAt: Long? = null
)

open class PhotoObject(
    var name: String? = null,
    var photo: String? = null,
    var type: String? = null,
    var sendedAt: Long? = null
)

data class Extras(
        val name: String,
        val photos: List<String>?
)