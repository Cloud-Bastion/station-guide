package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.config.ApplicationConfigProperties
import dev.aventix.station.authserver.user.authority.UserAuthority
import dev.aventix.station.authserver.user.authority.UserAuthorityCreateRequest
import dev.aventix.station.authserver.user.authority.UserAuthorityDto
import dev.aventix.station.authserver.user.authority.UserAuthorityRepository
import dev.aventix.station.authserver.user.spring.StationUserDetails
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.NoSuchElementException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authorityRepository: UserAuthorityRepository,
    private val passwordEncoder: PasswordEncoder,
    private val appProperties: ApplicationConfigProperties, // Keep for potential future use, but not for prefixing here
) : UserDetailsService {
    private val securityContextRepository = HttpSessionSecurityContextRepository()
    private val securityHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

    @PostConstruct
    @Transactional // Add transactional annotation for PostConstruct data initialization
    fun init() {
        // Check if user already exists to avoid errors on restart
        if (userRepository.findByEmail("test@gmail.com").isEmpty) {
            println("Creating initial test user...")
            try {
                createUser(
                    UserCreateRequest(
                        "test@gmail.com", "Raphael", "Eiden", "password", // Raw password
                        emptySet()
                    )
                )
                println("Initial test user created successfully.")
            } catch (e: Exception) {
                println("Error creating initial test user: ${e.message}")
                // Consider logging the stack trace
            }
        } else {
            println("Initial test user already exists.")
        }
    }

    fun findByEmail(email: String): Optional<UserDto> = userRepository.findByEmail(email).map { u -> u.toDto() }

    @Transactional // Add transactional annotation
    fun createAuthority(request: UserAuthorityCreateRequest): UserAuthorityDto {
        val entity = UserAuthority().apply {
            this.name = request.name
        }
        authorityRepository.saveAndFlush(entity)
        return entity.toDto()
    }

    @Transactional // Add transactional annotation
    @Throws(IllegalArgumentException::class)
    fun deleteAuthority(id: UUID) {
        authorityRepository.deleteById(id)
    }

    @Transactional // Add transactional annotation
    fun createUser(request: UserCreateRequest): UserDto {
        // Check if user already exists
        if (userRepository.findByEmail(request.email).isPresent) {
            // Handle appropriately - throw exception, return existing user, etc.
            // For now, let's print a message and potentially throw an error or return null/existing DTO
            println("User with email ${request.email} already exists.")
            // Depending on requirements, you might want to throw an exception:
            // throw EntityExistsException("User with email ${request.email} already exists.")
            // Or return the existing user's DTO:
            return userRepository.findByEmail(request.email).get().toDto()
        }

        val entity = User().apply {
            this.email = request.email
            this.firstName = request.firstName
            this.lastName = request.lastName
            // Correct way: Let the encoder handle the prefixing if needed (BCrypt does)
            this.password = passwordEncoder.encode(request.password)
            this.authorities = request.initialAuthorities.mapNotNull { authorityId ->
                // Use findByIdOrNull for safer handling if an authority ID is invalid
                authorityRepository.findById(authorityId).orElse(null)
            }.toMutableSet()
            // Assign a badge number (ensure this logic is robust, e.g., sequence or random unique)
            // Simple incrementing count might have race conditions without proper locking/sequences
            this.badgeNumber = userRepository.count() + 1 // Consider a more robust approach
        }
        userRepository.saveAndFlush(entity)
        println("Saved user ${entity.email} with encoded password: ${entity.password}") // Log encoded password
        return entity.toDto()
    }

    @Transactional // Add transactional annotation
    fun patchUser(request: UserPatchRequest): UserDto {
        TODO("not implemented yet")
    }

    @Transactional // Add transactional annotation
    @Throws(IllegalArgumentException::class)
    fun deleteUser(id: UUID) {
        userRepository.deleteById(id)
    }

    // TODO differentiate between email, badge number or other auth methods
    @Throws(EntityNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        println("Attempting to load user by username/email: $username") // Add logging
        val user = userRepository.findByEmail(username).orElseThrow {
            println("User not found for email: $username") // Add logging
            EntityNotFoundException("User not found.")
        }
        // Log the password hash being loaded for comparison (useful for debugging)
        println("User found: ${user.email}, Password Hash from DB: ${user.password}")
        val dto = user.toDto()
        return StationUserDetails(dto)
    }


    fun authenticateUser(
        username: String,
        password: String,
        authentication: Authentication,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        request.getSession(true)
        val context = securityHolderStrategy.createEmptyContext()
        context.authentication = authentication
        securityContextRepository.saveContext(context, request, response)
    }

}
