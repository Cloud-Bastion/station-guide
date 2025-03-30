package dev.aventix.station.authserver.user.session

import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode

fun OAuth2Authorization.Token<*>.toSessionToken() = let {
    when (token) {
        is OAuth2AuthorizationCode -> AuthorizationCodeSessionToken()
        is OAuth2AccessToken -> AccessTokenSessionToken()
        is OAuth2RefreshToken -> RefreshTokenSessionToken()
        is OidcIdToken -> OidcTokenSessionToken()
        else -> throw IllegalArgumentException("Unknown token type: ${token.javaClass}")
    }.also { it.readFromOAuth2Token(token) }
}

fun findGrantType(value: String) = when (value) {
    AuthorizationGrantType.AUTHORIZATION_CODE.value -> AuthorizationGrantType.AUTHORIZATION_CODE
    else -> throw IllegalArgumentException("Unknown grant type: $value")
}