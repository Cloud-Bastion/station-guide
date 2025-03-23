package dev.aventix.station.authserver.user

data class UserAuthenticateResponse(
    val token: String? = null,
    val error: Error? = null
)
