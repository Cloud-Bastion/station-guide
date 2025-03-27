package dev.aventix.station.authserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@ConfigurationProperties(prefix = "station.config.auth-server")
class ApplicationConfigProperties {
    lateinit var rsaPublicKey: RSAPublicKey
    lateinit var rsaPrivateKey: RSAPrivateKey
    lateinit var passwordPrefix: String

    var authProviders: AuthProviders = AuthProviders()

    class AuthProviders {
        var google: OAuthProvider = OAuthProvider()
        var github: OAuthProvider = OAuthProvider()
    }

    class OAuthProvider {
        lateinit var clientId: String
        lateinit var clientSecret: String
        var redirectUri: String? = null // Optional field for providers that require it
    }

}