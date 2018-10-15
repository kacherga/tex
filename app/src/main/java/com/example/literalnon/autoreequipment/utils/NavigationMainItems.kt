package com.example.literalnon.autoreequipment.utils

import com.example.literalnon.autoreequipment.fillData.FullPhotoFragment
import services.mobiledev.ru.cheap.navigation.IBaseItem
import services.mobiledev.ru.cheap.ui.main.comments.*

/**
 * Created by dmitry on 04.05.18.
 */
enum class NavigationMainItems : IBaseItem {
    LIST_ACTIVE_ENTRY_SCREEN {
        override var data: Any? = null

        override fun getTag() = "LIST_ACTIVE_ENTRY_SCREEN"

        override fun getPreviousEnumObject() = null

        override fun getFragment() = ActiveEntryFragment.newInstance(data as Boolean)
    },
    PARTNER_SETTINGS_SCREEN {
        override var data: Any? = null

        override fun getTag() = "PARTNER_SETTINGS_SCREEN"

        override fun getPreviousEnumObject() = null

        override fun getFragment() = AddPartnerFragment.newInstance()
    },
    ADD_ENTRY_SCREEN {
        override var data: Any? = null

        override fun getTag() = "ADD_ENTRY_SCREEN"

        override fun getPreviousEnumObject() = null

        override fun getFragment() = AddEntryFragment.newInstance()
    },
    ENTER_NAME_SCREEN {
        override var data: Any? = null

        override fun getTag() = "ENTER_NAME_SCREEN"

        override fun getPreviousEnumObject() = ADD_ENTRY_SCREEN

        override fun getFragment() = EnterNameFragment.newInstance()
    },
    FILL_ENTRY_SCREEN {
        override var data: Any? = null

        override fun getTag() = "FILL_ENTRY_SCREEN"

        override fun getPreviousEnumObject() = ENTER_NAME_SCREEN

        override fun getFragment() = FillDataFragment.newInstance()
    },
    EDIT_ENTRY_SCREEN {
        override var data: Any? = null

        override fun getTag() = "EDIT_ENTRY_SCREEN"

        override fun getPreviousEnumObject() = LIST_ACTIVE_ENTRY_SCREEN

        override fun getFragment() = FillDataFragment.newInstance(true)
    },
    FULL_PHOTO_SCREEN {
        override var data: Any? = null

        override fun getTag() = "FULL_PHOTO_SCREEN"

        override fun getPreviousEnumObject() = FILL_ENTRY_SCREEN

        override fun getFragment() = FullPhotoFragment.newInstance(data as String)
    }
}