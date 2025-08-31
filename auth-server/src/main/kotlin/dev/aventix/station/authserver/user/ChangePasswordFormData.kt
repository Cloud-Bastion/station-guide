package dev.aventix.station.authserver.user

data class ChangePasswordFormData(
    val password: String,
    val repeatedPassword: String,
)