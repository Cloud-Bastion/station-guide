package dev.aventix.station.authserver.user.authority

import java.io.Serializable
import java.util.UUID

data class UserAuthorityDto(
    var id: UUID?,
    var name: String,
): Serializable