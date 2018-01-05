package jm.desprez

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.BiDiBodyLens

class UserHandler(private val userDAO: UserDAO) {

    private val userIso = Iso<UserDTO, UserDB>(
            { dto -> UserDB(dto.login, dto.role.name) },
            { db -> UserDTO(db.login, Role.valueOf(db.role)) }
    )

    private val anonymizeUser: (UserDTO) -> UserDTO = userLoginLens.setter("anonymous")
    private val anonymizeBody: BiDiBodyLens<UserDTO> = userBody.with(anonymizeUser)

    fun get(login: String): HttpHandler = { _ ->
        val userDB = userDAO.get(login)

        if (userDB != null)
            Response(OK).with(anonymizeBody of userIso.swap(userDB))
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