package services.mobiledev.ru.cheap.data

/**
 * Created by dmitry on 07.05.18.
 */
data class User(
        val name: String,
        val phone: String
)

object LoginController {

    private val USER = ""

    var user: User? = null
        get() {
            if (field == null) {
                field = Prefs.load(USER, User::class.java)
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