package dev.aventix.station.authserver.user.authority

import java.util.UUID

data class UserAuthorityDto(
    val id: UUID,
    val name: String,
)