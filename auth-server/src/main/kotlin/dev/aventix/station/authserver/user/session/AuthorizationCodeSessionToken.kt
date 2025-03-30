package dev.aventix.station.authserver.user.session

import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode
import java.time.ZoneId
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("authorization_code")
class AuthorizationCodeSessionToken : SessionToken() {
    override fun writeToOAuth2Token() = OAuth2AuthorizationCode(
        value,
        issued?.atZone(ZoneId.systemDefault())?.toInstant(),
        expires?.atZone(ZoneId.systemDefault())?.toInstant()
    ) to metadata
}