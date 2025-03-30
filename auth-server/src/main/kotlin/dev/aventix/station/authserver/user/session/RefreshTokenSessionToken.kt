package dev.aventix.station.authserver.user.session

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import java.time.ZoneId

@Entity
@DiscriminatorValue("refresh_token")
class RefreshTokenSessionToken : SessionToken() {
    override fun writeToOAuth2Token() = OAuth2RefreshToken(
        value,
        issued?.atZone(ZoneId.systemDefault())?.toInstant(),
        expires?.atZone(ZoneId.systemDefault())?.toInstant()
    ) to metadata
}