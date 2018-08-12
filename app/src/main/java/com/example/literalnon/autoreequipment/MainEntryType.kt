package com.example.literalnon.autoreequipment

interface EntryType {
    val title: String
    val photosId: ArrayList<Int>
}

enum class MainEntryType : EntryType {
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
}

interface PhotoType {
    val title: String
    val typeColor: Int
}

enum class PHOTO_TYPE : PhotoType {
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
}

class Photo(
        val id: Int,
        val name: String,
        val type: PhotoType,
        var workType: ArrayList<EntryType>? = null,
        var photo: String? = null
)

val photos = arrayListOf<Photo>(
        Photo(0, "Хуйня на постном масле", PHOTO_TYPE.PHOTO_TYPE_1),
        Photo(1, "Общий вид автомобиля спереди с Гос. Знаком", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(2, "Общий вид сбоку", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(3, "Общий вид автомобиля сзади с Гос. знаком", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(4, "Подкапотное пространство", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(5, "Редуктор", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(6, "Блок управления", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(7, "Форсунки", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(8, "Газовый клапан (если есть)", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(9, "Бензиновый клапан (если есть)", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(10, "Проводка (должна быть в гофрированном кожухе)", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(11, "Баллон (на расстоянии)", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(12, "Бирка баллона (должен быть отчетливо виден номер)", PHOTO_TYPE.PHOTO_TYPE_1),
        Photo(13, "Вентиляция", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(14, "Мультиклапан", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(15, "ВЗУ", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(16, "Паспорт баллона", PHOTO_TYPE.PHOTO_TYPE_1),
        Photo(17, "Паспорт собственника первая страница", PHOTO_TYPE.PHOTO_TYPE_3),
        Photo(18, "Прописка собственника", PHOTO_TYPE.PHOTO_TYPE_3),
        Photo(19, "Паспорт представителя первая страница", PHOTO_TYPE.PHOTO_TYPE_3),
        Photo(20, "Прописка представителя", PHOTO_TYPE.PHOTO_TYPE_3),
        Photo(21, "Свидетельство регистрации автомобиля лицевая сторона", PHOTO_TYPE.PHOTO_TYPE_3),
        Photo(22, "Свидетельство регистрации автомобиля обратная сторона", PHOTO_TYPE.PHOTO_TYPE_3),
        Photo(23, "ПТС лицевая сторона", PHOTO_TYPE.PHOTO_TYPE_3),
        Photo(24, "ПТС обратная сторона", PHOTO_TYPE.PHOTO_TYPE_3),
        Photo(25, "Крупный план силового бампера", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(26, "Сертификат на силовой бампер", PHOTO_TYPE.PHOTO_TYPE_1),
        Photo(27, "Приложение к сертификату с указанием конкретной модели авто.", PHOTO_TYPE.PHOTO_TYPE_1),
        Photo(28, "Фаркоп (ТСУ) крупным планом", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(29, "Заводская маркировка Фаркопа (ТСУ) крупный план", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(30, "Паспорт на Фаркоп (ТСУ)", PHOTO_TYPE.PHOTO_TYPE_1),
        Photo(31, "Наружный блок холодильной установки", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(32, "Внутренний блок холодильной установки", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(33, "Крупный план заводской маркировки холодильной установки", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(34, "Общий вид внутреннего пространства кузова с местом расположения внутреннего блока", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(35, "Крупный план компрессора холодильной установки", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(36, "Паспорт на холодильную установку", PHOTO_TYPE.PHOTO_TYPE_1),
        Photo(37, "Сертификат на автомобильную установку", PHOTO_TYPE.PHOTO_TYPE_1),
        Photo(38, "Элементы крепления Двигателя в подкапотном пространстве", PHOTO_TYPE.PHOTO_TYPE_2),
        Photo(39, "Крупный план заводского номера Двигателя", PHOTO_TYPE.PHOTO_TYPE_2)
)

val allEntryType = arrayListOf<EntryType>(
        MainEntryType.TYPE_1,
        MainEntryType.TYPE_2,
        MainEntryType.TYPE_3,
        MainEntryType.TYPE_4,
        MainEntryType.TYPE_5,
        MainEntryType.TYPE_6
)
