package dev.aventix.station.authserver.user.role

data class UserRoleCreateRequest(
    val name: String,
    val displayName: String,
    val initialAuthorities: List<String>
)