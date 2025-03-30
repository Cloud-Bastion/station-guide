package dev.aventix.station.authserver.user.session

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.hibernate.annotations.Type
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2Token
import java.time.ZoneId

@Entity
@DiscriminatorValue("access_token")
class AccessTokenSessionToken : SessionToken() {
    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    var scopes: Set<String> = emptySet()

    override fun readFromOAuth2Token(token: OAuth2Token) {
        super.readFromOAuth2Token(token)
        scopes = (token as OAuth2AccessToken).scopes
    }

    override fun writeToOAuth2Token() = OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        value,
        issued?.atZone(ZoneId.systemDefault())?.toInstant(),
        expires?.atZone(ZoneId.systemDefault())?.toInstant(),
        scopes
    ) to metadata
}