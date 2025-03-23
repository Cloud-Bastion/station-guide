package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.config.ApplicationConfigProperties
import dev.aventix.station.authserver.user.authority.UserAuthority
import dev.aventix.station.authserver.user.authority.UserAuthorityCreateRequest
import dev.aventix.station.authserver.user.authority.UserAuthorityDto
import dev.aventix.station.authserver.user.authority.UserAuthorityRepository
import dev.aventix.station.authserver.user.spring.StationUserDetails
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authorityRepository: UserAuthorityRepository,
    private val passwordEncoder: PasswordEncoder,
    private val appProperties: ApplicationConfigProperties,
): UserDetailsService {

    @PostConstruct
    fun init() {
        createUser(
            UserCreateRequest(
            "test@station.airforce",
            "Raphael",
            "Eiden",
            "password",
                emptySet()
            )
        )
    }

    fun createAuthority(request: UserAuthorityCreateRequest): UserAuthorityDto {
        val entity = UserAuthority().apply {
            this.name = request.name
        }
        authorityRepository.saveAndFlush(entity)
        return entity.toDto()
    }

    @Throws(IllegalArgumentException::class)
    fun deleteAuthority(id: UUID) {
        authorityRepository.deleteById(id)
    }

    fun createUser(request: UserCreateRequest): UserDto {
        val entity = User().apply {
            this.email = request.email
            this.firstName = request.firstName
            this.lastName = request.lastName
            this.password = appProperties.passwordPrefix + passwordEncoder.encode(request.password)
            this.authProvider = AuthProvider.LOCAL
            this.authorities = request.initialAuthorities.map { authorityId ->
                authorityRepository.findById(authorityId).orElseThrow { NoSuchElementException("Authority does not exist.") }
            }.toMutableSet()
        }
        userRepository.saveAndFlush(entity)
        return entity.toDto()
    }

    fun patchUser(request: UserPatchRequest): UserDto {
        TODO("not implemented yet")
    }

    @Throws(IllegalArgumentException::class)
    fun deleteUser(id: UUID) {
        userRepository.deleteById(id)
    }

    // TODO differentiate between email, badge number or other auth methods
    @Throws(EntityNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val dto = userRepository.findByEmail(username).orElseThrow {
            EntityNotFoundException("User not found.")
        }.toDto()
        return StationUserDetails(dto)
    }

}