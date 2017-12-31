import org.http4k.core.*
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto

val userBody = Body.auto<UserDTO>().toLens()

class UserHandler(private val userDAO: UserDAO) {

    fun get(login: String): HttpHandler = { _ ->
        val userDB = userDAO.get(login)

        if (userDB != null)
            Response(OK).with(userBody of UserDTO(userDB.login, Role.valueOf(userDB.role)))
        else
            Response(NOT_FOUND).body("User $login not found")
    }

    fun put(login: String): HttpHandler = { request ->
        val userDTO = userBody(request)

        when (login) {
            "root" -> Response(BAD_REQUEST).body("root user cannot be updated")
            userDTO.login -> {
                userDAO.put(UserDB(login, userDTO.role.name))
                Response(NO_CONTENT)
            }
            else -> Response(BAD_REQUEST).body("wrong path")
        }
    }

    fun delete(login: String): HttpHandler = { _ ->
        if (login == "root") {
            Response(BAD_REQUEST).body("root user cannot be deleted")
        } else {
            userDAO.delete(login)
            Response(NO_CONTENT)
        }
    }
}