package dev.aventix.station.authserver.user.authority

import org.springframework.stereotype.Service

@Service
class UserAuthorityService(
    private val repo: UserAuthorityRepository,
) {
    fun createAuthoritiesIfNotExists(authorities: List<String>) {
        authorities.forEach { authority -> createAuthorityIfNotExists(authority) }
    }

    fun createAuthorityIfNotExists(name: String) {
        if (repo.findByName(name) != null) {
            println("User authority $name already exists.")
            return
        }

        val entity = UserAuthority().apply {
            this.name = name
        }
        repo.saveAndFlush(entity)
    }

}