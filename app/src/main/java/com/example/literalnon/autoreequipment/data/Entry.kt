package com.example.literalnon.autoreequipment.data

import com.example.literalnon.autoreequipment.utils.UpdateService.Companion.IMAGE_COMPRESS_QUALITY
import com.example.literalnon.autoreequipment.utils.UpdateService.Companion.IMAGE_MAX_SIZE
import io.realm.RealmList
import io.realm.RealmObject

open class Entry : RealmObject() {
    var name: String? = null
    var phone: String? = null
    var workTypes: RealmList<WorkType>? = null
    var sendType: Int = 0// 0 - не отправлено, 1 - отправлено, 2 отправлено частично
    var sendedAt: Long? = null
    var photos: RealmList<RealmPhoto>? = null
}

fun Entry.toObject(): EntryObject {
    return EntryObject(
            name,
            phone,
            workTypes?.map { it.toObject() },
            sendType,
            sendedAt,
            photos?.map { it.toObject() }
    )
}

open class WorkType : RealmObject() {
    var name: String? = null
    var description: String? = null
    var sendedAt: Long? = null
    var photosId: RealmList<Int>? = null
}

fun WorkType.toObject(): WorkTypeObject {
    return WorkTypeObject(
            name,
            //photos?.map { it.toObject() },
            description,
            sendedAt,
            photosId?.toList()
    )
}

open class RealmPhoto : RealmObject() {
    var photoId: Int? = null
    var name: String? = null
    var photo: String? = null
    var type: String? = null
    var sendedAt: Long? = null
}

fun RealmPhoto.toObject(): PhotoObject {
    return PhotoObject(
            photoId,
            name,
            photo,
            type,
            sendedAt
    )
}

open class EntryObject(
        var name: String? = null,
        var phone: String? = null,
        var workTypes: List<WorkTypeObject>? = null,
        var sendType: Int = 0,// 0 - не отправлено, 1 - отправлено, 2 отправлено частично
        var sendedAt: Long? = null,
        var photos: List<PhotoObject>? = null
)

open class WorkTypeObject(
        var name: String? = null,
        var description: String? = null,
        var sendedAt: Long? = null,
        var photosId: List<Int>? = null
)

open class PhotoObject(
        var photoId: Int? = null,
        var name: String? = null,
        var photo: String? = null,
        var type: String? = null,
        var sendedAt: Long? = null
)

data class Extras(
        val name: String,
        val photos: List<String>?
)