package dev.aventix.station.authserver.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository: JpaRepository<User, UUID> {
    fun findByEmail(email: String): Optional<User>
}