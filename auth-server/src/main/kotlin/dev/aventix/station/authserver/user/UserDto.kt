package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.user.authority.UserAuthorityDto
import dev.aventix.station.authserver.user.role.UserRoleDto
import java.io.Serializable
import java.util.UUID

data class UserDto(
    val id: UUID,
    val badgeNumber: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val authorities: Set<UserAuthorityDto>,
    val roles: Set<UserRoleDto>,
): Serializable