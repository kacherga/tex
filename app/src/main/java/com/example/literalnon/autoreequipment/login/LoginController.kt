package services.mobiledev.ru.cheap.data

/**
 * Created by dmitry on 07.05.18.
 */
object LoginController {

    private val USER = ""

    var user: String? = ""
        get() {
            if (field == null || field?.isEmpty() == true) {
                field = Prefs.load(USER, String::class.java) ?: ""
            }

            return field
        }
        set(value) {
            field = value
            Prefs.save(USER, value)
        }

    fun logOut() {
        clearPrefs()
    }

    fun clearUser() {
        Prefs.edit()
                .remove(USER)
                .apply()

        user = null
    }

    private fun clearPrefs() {
        clearUser()
    }
}