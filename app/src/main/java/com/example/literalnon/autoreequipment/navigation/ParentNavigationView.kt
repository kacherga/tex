package services.mobiledev.ru.cheap.navigation

/**
 * Created by dmitry on 22.11.17.
 */
interface ParentNavigationView {
    fun navigateNext(enumObject: IBaseItem, data: Any? = null)
    fun navigateBack()
    fun navigationToFirst()
    fun updateUi(enumObject: IBaseItem?)
}
