package dev.aventix.station.authserver.token

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class AuthTokenCustomizer: OAuth2TokenCustomizer<JwtEncodingContext> {
    override fun customize(context: JwtEncodingContext) {
        val principal: Authentication = context.getPrincipal()
        if (context.tokenType == OAuth2TokenType.ACCESS_TOKEN) {
            val authorities: Set<String> = principal.authorities.stream()
                .map { obj: GrantedAuthority -> obj.authority }
                .collect(Collectors.toSet())
            context.claims.claim("scope", authorities) // Add existing authorities
            if (principal is OAuth2User) {
                val oAuth2User = principal as OAuth2User
                context.claims.claim("full_name", oAuth2User.getAttribute("name"))
                context.claims.claim("picture", oAuth2User.getAttribute("picture"))
            }
        }
    }
}