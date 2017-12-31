enum class Role {
    ADMIN, REGULAR
}

data class UserDTO(val login: String, val role: Role)

val userRoleLens = Lens(UserDTO::role, { newValue -> { user -> user.copy(role = newValue) } })

val userLoginLens = Lens(UserDTO::login, { newValue -> { user -> user.copy(login = newValue) } })

val userUpperCaseLoginGetter: (UserDTO) -> String = userLoginLens.mapGetter { toUpperCase() }

val mapToUpperCaseLogin: (UserDTO) -> UserDTO = userLoginLens.map { toUpperCase() }