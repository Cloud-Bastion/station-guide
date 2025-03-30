package dev.aventix.station.authserver.user.session

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.hibernate.annotations.Type
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import java.time.ZoneId

@Entity
@DiscriminatorValue("oidc_token")
class OidcTokenSessionToken : SessionToken() {
    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    var claims: Map<String, Any> = emptyMap()

    override fun readFromOAuth2Token(token: OAuth2Token) {
        super.readFromOAuth2Token(token)
        claims = (token as OidcIdToken).claims
    }

    override fun writeToOAuth2Token() = OidcIdToken(
        value,
        issued?.atZone(ZoneId.systemDefault())?.toInstant(),
        expires?.atZone(ZoneId.systemDefault())?.toInstant(),
        claims
    ) to metadata
}