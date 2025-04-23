package dev.aventix.station.authserver.user.role

import dev.aventix.station.authserver.user.authority.UserAuthorityDto
import java.io.Serializable
import java.util.UUID

data class UserRoleDto(
    var id: UUID?,
    var name: String,
    var authorities: List<UserAuthorityDto>,
): Serializable
