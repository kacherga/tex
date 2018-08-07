package com.example.literalnon.autoreequipment.data

import io.realm.RealmList
import io.realm.RealmObject

open class Entry : RealmObject() {
    var name: String? = null
    var workTypes: RealmList<WorkType>? = null
}

open class WorkType : RealmObject() {
    var name: String? = null
    var photos: RealmList<RealmPhoto>? = null
}

open class RealmPhoto : RealmObject() {
    var name: String? = null
    var photo: String? = null
    var type: String? = null
}