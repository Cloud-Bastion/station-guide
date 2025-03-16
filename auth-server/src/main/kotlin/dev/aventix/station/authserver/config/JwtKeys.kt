package dev.aventix.station.authserver.config

import com.nimbusds.jose.jwk.RSAKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.UUID

class JwtKeys {
    companion object {
        fun generateRsa(): RSAKey {
            val keyPair = KeyGeneratorUtils.generateRsaKey()
            val publicKey = keyPair.public as RSAPublicKey
            val privateKey = keyPair.private as RSAPrivateKey
            return RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build()
        }
    }
}