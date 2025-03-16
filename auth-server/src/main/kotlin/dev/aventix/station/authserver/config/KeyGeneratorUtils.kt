package dev.aventix.station.authserver.config

import org.springframework.stereotype.Component
import java.security.KeyPair
import java.security.KeyPairGenerator

@Component
class KeyGeneratorUtils {
    companion object {
        @Throws(IllegalStateException::class)
        fun generateRsaKey(): KeyPair {
            try {
                val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
                keyPairGenerator.initialize(2048)
                return keyPairGenerator.generateKeyPair()
            } catch (e: Exception) {
                throw IllegalStateException("")
            }
        }
    }
}