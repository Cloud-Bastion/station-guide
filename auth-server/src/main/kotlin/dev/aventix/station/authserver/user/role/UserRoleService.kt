package dev.aventix.station.authserver.user.role

import dev.aventix.station.authserver.user.authority.UserAuthorityRepository
import jakarta.persistence.EntityExistsException
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class UserRoleService(
    private val roleRepo: UserRoleRepository,
    private val authorityRepository: UserAuthorityRepository,
) {

    @Throws(EntityExistsException::class)
    fun createRole(request: UserRoleCreateRequest) {
        if (roleRepo.findByName(request.name) != null) {
            throw EntityExistsException("Role already exists")
        }

        val entity = UserRole().apply {
            this.name = request.name
            this.authorities = request.initialAuthorities.mapNotNull {
                authorityRepository.findByName(it)
            }.toMutableSet()
        }

        roleRepo.saveAndFlush(entity)
    }

}