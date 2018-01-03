
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.with

class UserClient(scheme: String = "http", host: String = "localhost", port: Int = 80) {

    private val get = Request(Method.GET, "$scheme://$host:$port/api/v1/users/{login}")
    private val put = get.method(Method.PUT)
    private val delete = get.method(Method.DELETE)

    fun get(login: String) = get.with(loginPath of login)
    fun put(userDTO: UserDTO, pathParam: String = userDTO.login) = put.with(loginPath of pathParam, userBody of userDTO)
    fun delete(login: String) = delete.with(loginPath of login)

    fun delete(userDTO: UserDTO) = delete(userDTO.login)
}