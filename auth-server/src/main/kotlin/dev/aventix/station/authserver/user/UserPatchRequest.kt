package dev.aventix.station.authserver.user

import java.util.UUID

data class UserPatchRequest(
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val password: String?,
    val authorities: Set<UUID>?,
)
