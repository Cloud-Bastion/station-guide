package dev.aventix.station.authserver.user.spring

import dev.aventix.station.authserver.user.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class StationAuthentication(private val account: User) : Authentication {
    override fun getName(): String = account.email

    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun getCredentials(): Any = account

    override fun getDetails(): Any = StationUserDetails(account.toDto())

    override fun getPrincipal(): Any = account

    override fun isAuthenticated(): Boolean = true

    override fun setAuthenticated(isAuthenticated: Boolean) { }
}