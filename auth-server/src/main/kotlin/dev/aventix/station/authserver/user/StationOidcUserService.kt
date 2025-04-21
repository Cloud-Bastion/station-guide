package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.user.spring.StationUserDetails
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class StationOidcUserService(
    private val userService: UserService,
) : OidcUserService() {

    @Override
    @Transactional
    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val user = super.loadUser(userRequest)
        val email = user.email
        val localUser = userService.findByEmail(email)

        // deny login for unknown users
        if (localUser.isEmpty) {
            throw OAuth2AuthenticationException("Email address not allowed.")
        }

        // correctly map authorities to user
        return DefaultOidcUser(
            StationUserDetails(localUser.get()).authorities, user.idToken, user.userInfo, "email"
        )
    }

}