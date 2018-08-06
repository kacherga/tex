package com.example.literalnon.autoreequipment.utils

import services.mobiledev.ru.cheap.navigation.IBaseItem
import services.mobiledev.ru.cheap.ui.main.comments.AddEntryFragment
import services.mobiledev.ru.cheap.ui.main.comments.EnterNameFragment
import services.mobiledev.ru.cheap.ui.main.comments.FillDataFragment

/**
 * Created by dmitry on 04.05.18.
 */
enum class NavigationMainItems : IBaseItem {
    LIST_ACTIVE_ENTRY_SCREEN {
        override var data: Any? = null

        override fun getTag() = "LIST_ACTIVE_ENTRY_SCREEN"

        override fun getPreviousEnumObject() = null

        override fun getFragment() = AddEntryFragment.newInstance()
    },
    PARTNER_SETTINGS_SCREEN {
        override var data: Any? = null

        override fun getTag() = "PARTNER_SETTINGS_SCREEN"

        override fun getPreviousEnumObject() = null

        override fun getFragment() = AddEntryFragment.newInstance()
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
    }
}