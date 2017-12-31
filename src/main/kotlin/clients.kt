import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.with

class UserClient(private val basePath: String) {
    private val usersPath = "users"

    fun get(login: String) = Request(Method.GET, "$basePath/api/v1/$usersPath/$login")
    fun put(userDTO: UserDTO, pathParam: String = userDTO.login) = Request(Method.PUT, "$basePath/api/v1/$usersPath/$pathParam").with(userBody of userDTO)
    fun delete(login: String) = Request(Method.DELETE, "$basePath/api/v1/$usersPath/$login")

    fun delete(userDTO: UserDTO) = delete(userDTO.login)
}