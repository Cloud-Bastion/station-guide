package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.user.authority.UserAuthorityCreateRequest
import java.util.UUID

data class UserCreateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val initialAuthorities: Set<UUID>
)