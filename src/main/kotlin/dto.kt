enum class Role {
    ADMIN, REGULAR
}

data class UserDTO(val login: String, val role: Role)

