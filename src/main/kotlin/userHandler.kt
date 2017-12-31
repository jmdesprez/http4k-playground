import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto

val userBody = Body.auto<UserDTO>().toLens()

class UserHandler(private val userDAO: UserDAO) {

    private val userIso = Iso<UserDTO, UserDB>(
            { dto -> UserDB(dto.login, dto.role.name) },
            { db -> UserDTO(db.login, Role.valueOf(db.role)) }
    )

    fun get(login: String): HttpHandler = { _ ->
        val userDB = userDAO.get(login)

        if (userDB != null)
            Response(OK).with(userBody of userIso.swap(userDB))
        else
            Response(NOT_FOUND).body("User $login not found")
    }

    fun put(login: String): HttpHandler = { request ->
        val userDTO = userBody(request)

        when (login) {
            "root" -> Response(BAD_REQUEST).body("root user cannot be updated")
            userDTO.login -> {
                userDAO.put(userIso(userDTO))
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