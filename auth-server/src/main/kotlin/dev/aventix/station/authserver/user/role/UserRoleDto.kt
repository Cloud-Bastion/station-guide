package dev.aventix.station.authserver.user.role

import dev.aventix.station.authserver.user.authority.UserAuthorityDto
import java.io.Serializable
import java.util.UUID

data class UserRoleDto(
    val id: UUID,
    val name: String,
    val authorities: List<UserAuthorityDto>,
): Serializable
