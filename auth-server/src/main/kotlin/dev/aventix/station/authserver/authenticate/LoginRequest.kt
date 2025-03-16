package dev.aventix.station.authserver.authenticate

data class LoginRequest(
    val username: String,
    val password: String,
)
