package dev.aventix.station.authserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@ConfigurationProperties(prefix = "station.config.auth-server")
class ApplicationConfigProperties {
    lateinit var rsaPublicKey: RSAPublicKey
    lateinit var rsaPrivateKey: RSAPrivateKey
    lateinit var passwordPrefix: String
}