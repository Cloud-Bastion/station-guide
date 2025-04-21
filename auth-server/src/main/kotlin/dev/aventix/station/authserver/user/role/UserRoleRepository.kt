package dev.aventix.station.authserver.user.role

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRoleRepository: JpaRepository<UserRole, UUID> {
    fun findByName(name: String): UserRole?
}