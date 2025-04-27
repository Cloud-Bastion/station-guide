package dev.aventix.station.authserver.user.role

import dev.aventix.station.authserver.user.authority.UserAuthority
import dev.aventix.station.authserver.user.authority.UserAuthorityRepository
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class UserRoleService(
    private val roleRepo: UserRoleRepository,
    private val authorityRepository: UserAuthorityRepository,
) {
    @Throws(EntityExistsException::class)
    fun createRole(userRole: UserRoleDto): UserRoleDto {
        if (roleRepo.findByName(userRole.name) != null) {
            throw EntityExistsException("Role already exists")
        }

        val entity = UserRole().apply {
            this.name = userRole.name
            this.displayName = userRole.displayName
            this.authorities = userRole.authorities.mapNotNull {
                authorityRepository.findByName(it.name)
            }.toMutableSet()
        }

        return roleRepo.saveAndFlush(entity).toDto()
    }

    fun getAllRoles(): List<UserRoleDto> {
        return this.roleRepo.findAll().map { role -> role.toDto() }
    }

    @Throws(EntityNotFoundException::class)
    fun getRoleByName(name: String): UserRoleDto {
        val findByName = this.roleRepo.findByName(name) ?: throw EntityNotFoundException("Role does not exists")
        return findByName.toDto()
    }

    @Throws(EntityNotFoundException::class, EntityExistsException::class)
    fun addAuthorityToRole(name: String, authority: String): UserRoleDto {
        val role = this.roleRepo.findByName(name) ?: throw EntityNotFoundException("Role does not exists")

        if (role.authorities.any { it.name == authority }) throw EntityExistsException("authority $authority already exists in role $name")

        val authorityEntity = this.authorityRepository.findByName(authority) ?: this.authorityRepository.saveAndFlush(
            UserAuthority().apply { this.name = name })

        role.authorities.add(authorityEntity)
        return this.roleRepo.saveAndFlush(role).toDto()
    }

    @Throws(EntityNotFoundException::class)
    fun removeAuthorityToRole(name: String, authority: String): UserRoleDto {
        val role = this.roleRepo.findByName(name) ?: throw EntityNotFoundException("Role does not exists")

        if (!role.authorities.any { it.name == authority }) throw EntityNotFoundException("authority $authority dont exist in role $name")

        val authorityEntity = this.authorityRepository.findByName(authority)
            ?: throw EntityNotFoundException("authority $authority dont exist")

        role.authorities.remove(authorityEntity)
        return this.roleRepo.saveAndFlush(role).toDto()
    }

    @Throws(EntityNotFoundException::class)
    fun setAuthoritiesToRole(name: String, authorities: List<String>): UserRoleDto {
        val role = this.roleRepo.findByName(name) ?: throw EntityNotFoundException("Role does not exists")

        role.authorities.clear()

        authorities.forEach {
            val authorityEntity = this.authorityRepository.findByName(it) ?: this.authorityRepository.saveAndFlush(
                UserAuthority().apply { this.name = name })
            role.authorities.add(authorityEntity)
        }

        return this.roleRepo.saveAndFlush(role).toDto()
    }
}