package dev.aventix.station.authserver.user.session

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository("customSessionRepository")
interface SessionRepository:
    JpaRepository<SpringSession, String>,
    PagingAndSortingRepository<SpringSession, String> {

    fun findByPrincipalName(principalName: String): SpringSession?

}