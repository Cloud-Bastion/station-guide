package dev.aventix.station.authserver.user.spring

import dev.aventix.station.authserver.user.UserDto
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class StationUserDetails(private val userData: UserDto): UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return userData.authorities
            .map { authority ->
                SimpleGrantedAuthority(authority.name)
            }
            .toCollection(ArrayList())
    }

    override fun getPassword(): String {
        return userData.password
    }

    override fun getUsername(): String {
        return userData.email
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

}