package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.provider.AuthProvider
import dev.aventix.station.authserver.user.authority.UserAuthorityDto
import dev.aventix.station.authserver.user.session.Session
import java.util.UUID

data class UserDto(
    val id: UUID,
    val badgeNumber: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val authorities: Set<UserAuthorityDto>,
    val authProvider: AuthProvider,
    val oAuthId: String?,
    val sessions: List<Session>,
)