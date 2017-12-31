import org.funktionale.composition.andThen
import org.http4k.core.Body
import org.http4k.core.HttpMessage
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens

enum class Role {
    ADMIN, REGULAR
}

data class UserDTO(val login: String, val role: Role)

val userBody: BiDiBodyLens<UserDTO> = Body.auto<UserDTO>().toLens()

val userRoleLens = Lens(UserDTO::role, { newValue -> { user -> user.copy(role = newValue) } })

val userLoginLens = Lens(UserDTO::login, { newValue -> { user -> user.copy(login = newValue) } })

val userUpperCaseLoginGetter: (UserDTO) -> String = userLoginLens.mapGetter { toUpperCase() }

val mapToUpperCaseLogin: (UserDTO) -> UserDTO = userLoginLens.map { toUpperCase() }

val userLogin: (HttpMessage) -> String = userBody.andThen(UserDTO::login)

val requestLoginLens: Lens<HttpMessage, String> = userBody.chain(userLoginLens)
