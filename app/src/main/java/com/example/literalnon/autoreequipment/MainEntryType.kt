package com.example.literalnon.autoreequipment

data class EntryType(
        val title: String,
        val photosId: ArrayList<Int>
)

/*enum class MainEntryType : EntryType {
    TYPE_1 {
        override val title = "Установка ГБО"
        override val photosId = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)
    },
    TYPE_2 {
        override val title = "Установка силового бампера"
        override val photosId = arrayListOf(1, 2, 3, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27)
    },
    TYPE_3 {
        override val title = "Установка Фаркопа (ТСУ)"
        override val photosId = arrayListOf(1, 2, 3, 17, 18, 19, 20, 21, 22, 23, 24, 28, 29, 30)
    },
    TYPE_4 {
        override val title = "Установка Рефрижератора"
        override val photosId = arrayListOf(1, 2, 3, 4, 17, 18, 19, 20, 21, 22, 23, 24, 31, 32, 33, 34, 35, 36, 37)
    },
    TYPE_5 {
        override val title = "Установка Двигателя"
        override val photosId = arrayListOf(1, 2, 3, 4, 17, 18, 19, 20, 21, 22, 23, 24, 38, 39)
    },
    TYPE_6 {
        override val title = "Прочее переоборудование"
        override val photosId = arrayListOf(1, 2, 3, 17, 18, 19, 20, 21, 22, 23, 24)
    }
}*/

data class PhotoType(
        val title: String,
        val typeColor: Int
)

const val EXTRA_PHOTO_TITLE = "Доп"

var allPhotoTypes = arrayListOf<PhotoType>(
        PhotoType(
                "Fake",
                android.R.color.holo_purple
        ),
        PhotoType(
                "ДОКУМЕНТЫ НА ОБОРУДОВАНИЕ",
                android.R.color.holo_green_dark
        ),
        PhotoType(
                "ФОТО АВТОМОБИЛЯ",
                android.R.color.holo_orange_light
        ),
        PhotoType(
                "ДОКУМЕНТЫ СОБСТВЕННИКА",
                android.R.color.holo_blue_dark
        ),
        PhotoType(
                "ДОПОЛНИТЕЛЬНЫЕ ДАННЫЕ",
                android.R.color.holo_orange_dark
        )
)

/*enum class PHOTO_TYPE : PhotoType {
    PHOTO_TYPE_1 {
        override val title: String = "ДОКУМЕНТЫ НА ОБОРУДОВАНИЕ"
        override val typeColor: Int = android.R.color.holo_green_dark
    },
    PHOTO_TYPE_2 {
        override val title: String = "ФОТО АВТОМОБИЛЯ"
        override val typeColor: Int = android.R.color.holo_orange_light
    },
    PHOTO_TYPE_3 {
        override val title: String = "ДОКУМЕНТЫ СОБСТВЕННИКА"
        override val typeColor: Int = android.R.color.holo_blue_dark
    },
    PHOTO_TYPE_4 {
        override val title: String = "ДОПОЛНИТЕЛЬНЫЕ ДАННЫЕ"
        override val typeColor: Int = android.R.color.holo_orange_dark
    }
}*/

class Photo(
        val id: Int,
        val name: String,
        val type: PhotoType,
        var photoCount: Int? = 1,
        var workType: ArrayList<EntryType>? = null,
        var photos: ArrayList<String>? = null
)

var photos = arrayListOf<Photo>(
        Photo(0, "Неизвестный тип", allPhotoTypes[1]),
        Photo(1, "Общий вид автомобиля спереди с Гос. Знаком", allPhotoTypes[2]),
        Photo(2, "Общий вид сбоку", allPhotoTypes[2]),
        Photo(3, "Общий вид автомобиля сзади с Гос. знаком", allPhotoTypes[2]),
        Photo(4, "Подкапотное пространство", allPhotoTypes[2]),
        Photo(5, "Редуктор", allPhotoTypes[2]),
        Photo(6, "Блок управления", allPhotoTypes[2]),
        Photo(7, "Форсунки", allPhotoTypes[2]),
        Photo(8, "Газовый клапан (если есть)", allPhotoTypes[2]),
        Photo(9, "Бензиновый клапан (если есть)", allPhotoTypes[2]),
        Photo(10, "Проводка (должна быть в гофрированном кожухе)", allPhotoTypes[2]),
        Photo(11, "Баллон (на расстоянии)", allPhotoTypes[2], 8),
        Photo(12, "Бирка баллона (должен быть отчетливо виден номер)", allPhotoTypes[1]),
        Photo(13, "Вентиляция", allPhotoTypes[2]),
        Photo(14, "Мультиклапан", allPhotoTypes[2]),
        Photo(15, "ВЗУ", allPhotoTypes[2]),
        Photo(16, "Паспорт баллона", allPhotoTypes[1]),
        Photo(17, "Паспорт собственника первая страница", allPhotoTypes[3]),
        Photo(18, "Прописка собственника", allPhotoTypes[3]),
        Photo(19, "Паспорт представителя первая страница", allPhotoTypes[3]),
        Photo(20, "Прописка представителя", allPhotoTypes[3]),
        Photo(21, "Свидетельство регистрации автомобиля лицевая сторона", allPhotoTypes[3]),
        Photo(22, "Свидетельство регистрации автомобиля обратная сторона", allPhotoTypes[3]),
        Photo(23, "ПТС лицевая сторона", allPhotoTypes[3]),
        Photo(24, "ПТС обратная сторона", allPhotoTypes[3]),
        Photo(25, "Крупный план силового бампера", allPhotoTypes[2]),
        Photo(26, "Сертификат на силовой бампер", allPhotoTypes[1]),
        Photo(27, "Приложение к сертификату с указанием конкретной модели авто.", allPhotoTypes[1]),
        Photo(28, "Фаркоп (ТСУ) крупным планом", allPhotoTypes[2]),
        Photo(29, "Заводская маркировка Фаркопа (ТСУ) крупный план", allPhotoTypes[2]),
        Photo(30, "Паспорт на Фаркоп (ТСУ)", allPhotoTypes[1]),
        Photo(31, "Наружный блок холодильной установки", allPhotoTypes[2]),
        Photo(32, "Внутренний блок холодильной установки", allPhotoTypes[2]),
        Photo(33, "Крупный план заводской маркировки холодильной установки", allPhotoTypes[2]),
        Photo(34, "Общий вид внутреннего пространства кузова с местом расположения внутреннего блока", allPhotoTypes[2]),
        Photo(35, "Крупный план компрессора холодильной установки", allPhotoTypes[2]),
        Photo(36, "Паспорт на холодильную установку", allPhotoTypes[1]),
        Photo(37, "Сертификат на автомобильную установку", allPhotoTypes[1]),
        Photo(38, "Элементы крепления Двигателя в подкапотном пространстве", allPhotoTypes[2]),
        Photo(39, "Крупный план заводского номера Двигателя", allPhotoTypes[2])
)

var allEntryType = arrayListOf<EntryType>(
        /*MainEntryType.TYPE_1,
        MainEntryType.TYPE_2,
        MainEntryType.TYPE_3,
        MainEntryType.TYPE_4,
        MainEntryType.TYPE_5,
        MainEntryType.TYPE_6*/
        EntryType(
                "Fake",
                arrayListOf()
        ),
        EntryType(
                "Установка ГБО",
                arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)
        ),
        EntryType(
                "Установка силового бампера",
                arrayListOf(1, 2, 3, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27)
        ),
        EntryType(
                "Установка Фаркопа (ТСУ)",
                arrayListOf(1, 2, 3, 17, 18, 19, 20, 21, 22, 23, 24, 28, 29, 30)
        ),
        EntryType(
                "Установка Рефрижератора",
                arrayListOf(1, 2, 3, 4, 17, 18, 19, 20, 21, 22, 23, 24, 31, 32, 33, 34, 35, 36, 37)
        ),
        EntryType(
                "Установка Двигателя",
                arrayListOf(1, 2, 3, 4, 17, 18, 19, 20, 21, 22, 23, 24, 38, 39)
        ),
        EntryType(
                "Прочее переоборудование",
                arrayListOf(1, 2, 3, 17, 18, 19, 20, 21, 22, 23, 24)
        )
)

/*
  photos: [{"id":0,"name":"Неизвестный тип","type":{"title":"ДОКУМЕНТЫ НА ОБОРУДОВАНИЕ","typeColor":17170453}},{"id":1,"name":"Общий вид автомобиля спереди с Гос. Знаком","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":2,"name":"Общий вид сбоку","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":3,"name":"Общий вид автомобиля сзади с Гос. знаком","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":4,"name":"Подкапотное пространство","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":5,"name":"Редуктор","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":6,"name":"Блок управления","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":7,"name":"Форсунки","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":8,"name":"Газовый клапан (если есть)","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":9,"name":"Бензиновый клапан (если есть)","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":10,"name":"Проводка (должна быть в гофрированном кожухе)","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":11,"name":"Баллон (на расстоянии)","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":12,"name":"Бирка баллона (должен быть отчетливо виден номер)","type":{"title":"ДОКУМЕНТЫ НА ОБОРУДОВАНИЕ","typeColor":17170453}},{"id":13,"name":"Вентиляция","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":14,"name":"Мультиклапан","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":15,"name":"ВЗУ","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":16,"name":"Паспорт баллона","type":{"title":"ДОКУМЕНТЫ НА ОБОРУДОВАНИЕ","typeColor":17170453}},{"id":17,"name":"Паспорт собственника первая страница","type":{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451}},{"id":18,"name":"Прописка собственника","type":{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451}},{"id":19,"name":"Паспорт представителя первая страница","type":{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451}},{"id":20,"name":"Прописка представителя","type":{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451}},{"id":21,"name":"Свидетельство регистрации автомобиля лицевая сторона","type":{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451}},{"id":22,"name":"Свидетельство регистрации автомобиля обратная сторона","type":{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451}},{"id":23,"name":"ПТС лицевая сторона","type":{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451}},{"id":24,"name":"ПТС обратная сторона","type":{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451}},{"id":25,"name":"Крупный план силового бампера","type":{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456}},{"id":26,"name":"Сертификат на силовой бампер","type":{"title":"ДОКУМЕНТЫ НА ОБОРУДОВАНИЕ","typeColor":17170453}},{"id":27,"name":"Приложение к сертификату с указанием конкретной модели авто.","type"

  photoTypes: [{"title":"Fake","typeColor":17170458},{"title":"ДОКУМЕНТЫ НА ОБОРУДОВАНИЕ","typeColor":17170453},{"title":"ФОТО АВТОМОБИЛЯ","typeColor":17170456},{"title":"ДОКУМЕНТЫ СОБСТВЕННИКА","typeColor":17170451},{"title":"ДОПОЛНИТЕЛЬНЫЕ ДАННЫЕ","typeColor":17170457}]

entryTypes : [{"photosId":[],"title":"Fake"},{"photosId":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24],"title":"Установка ГБО"},{"photosId":[1,2,3,16,17,18,19,20,21,22,23,24,25,26,27],"title":"Установка силового бампера"},{"photosId":[1,2,3,17,18,19,20,21,22,23,24,28,29,30],"title":"Установка Фаркопа (ТСУ)"},{"photosId":[1,2,3,4,17,18,19,20,21,22,23,24,31,32,33,34,35,36,37],"title":"Установка Рефрижератора"},{"photosId":[1,2,3,4,17,18,19,20,21,22,23,24,38,39],"title":"Установка Двигателя"},{"photosId":[1,2,3,17,18,19,20,21,22,23,24],"title":"Прочее переоборудование"}]

  */