package dev.aventix.station.authserver.user.session

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SessionTokenRepository : JpaRepository<SessionToken, UUID> {
    @Query("select t.session from AuthorizationCodeSessionToken t where t.value = :token")
    fun findByAuthorizationCode(token: String): Session?

    @Query("select t.session from AccessTokenSessionToken t where t.value = :token")
    fun findByAccessToken(token: String): Session?

    @Query("select t.session from RefreshTokenSessionToken t where t.value = :token")
    fun findByRefreshToken(token: String): Session?

    @Query("select t.session from OidcTokenSessionToken t where t.value = :token")
    fun findByIdToken(token: String): Session?
}