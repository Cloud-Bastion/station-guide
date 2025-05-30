package dev.aventix.station.authserver.user.request

data class UserCreateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val initialAuthorities: Set<String>,
    val roles: Set<String>
)