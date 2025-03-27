package dev.aventix.station.authserver.provider

interface AuthProviderDetailsService {
    fun getUserDetails(accessToken: String): AuthProviderDetails
}