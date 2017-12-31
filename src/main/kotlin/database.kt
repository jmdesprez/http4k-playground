data class UserDB(val login: String, val role: String)

interface UserDAO {
    fun get(login: String): UserDB?
    fun put(user: UserDB)
    fun delete(login: String)
}

class HashMapDAO : UserDAO {
    private val data = mutableMapOf<String, UserDB>()

    override fun get(login: String): UserDB? = data[login]

    override fun put(user: UserDB) {
        data[user.login] = user
    }

    override fun delete(login: String) {
        data.remove(login)
    }
}