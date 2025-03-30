package dev.aventix.station.authserver.user.session

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SessionRepository : JpaRepository<Session, UUID> {
    fun findByState(state: String): Session?

    @Query("from Session s join s.tokens t where s.state = :token or t.value = :token")
    fun findByStateOrToken(token: String): Session?
}