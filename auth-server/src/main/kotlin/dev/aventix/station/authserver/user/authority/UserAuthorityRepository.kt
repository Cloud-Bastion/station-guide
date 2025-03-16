package dev.aventix.station.authserver.user.authority

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserAuthorityRepository: JpaRepository<UserAuthority, UUID> {
}