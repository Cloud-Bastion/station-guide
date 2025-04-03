package dev.aventix.station.authserver.user.spring

import dev.aventix.station.authserver.user.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class StationAuthentication(val account: User) : Authentication {
    override fun getName(): String = account.email

    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun getCredentials() = account

    override fun getDetails() = account

    override fun getPrincipal() = account

    override fun isAuthenticated() = true

    override fun setAuthenticated(isAuthenticated: Boolean) { }
}