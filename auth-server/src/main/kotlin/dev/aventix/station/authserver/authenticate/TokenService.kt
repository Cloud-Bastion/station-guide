package dev.aventix.station.authserver.authenticate

import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class TokenService(
    private val encoder: JwtEncoder,
    private val decoder: JwtDecoder,
) {

    @Throws(JwtEncodingException::class)
    fun generateToken(authentication: Authentication): String {
        val now = Instant.now()
        val authorities = authentication.authorities.map { authority -> authority.authority }.joinToString(",")
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(1, ChronoUnit.HOURS))
            .subject(authentication.name)
            .claim("scope", authorities)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }

    fun extractUsername(token: String): String {
        val jwt = decoder.decode(token)
        return jwt.subject
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        return extractUsername(token) == userDetails.username
    }

}