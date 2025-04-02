package dev.aventix.station.authserver.controller

import dev.aventix.station.authserver.authenticate.LoginRequest
import dev.aventix.station.authserver.authenticate.TokenService
import dev.aventix.station.authserver.config.ApplicationConfigProperties
import dev.aventix.station.authserver.provider.google.GoogleProfileDetailsService
import dev.aventix.station.authserver.user.UserAuthenticateResponse
import dev.aventix.station.authserver.user.UserCreateRequest
import dev.aventix.station.authserver.user.UserDto
import dev.aventix.station.authserver.user.UserService
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityExistsException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@Validated
@RestController
@RequestMapping("/api/v1/auth")
class LoginRegistrationController(
    // AuthenticationManager and TokenService removed as the custom /login endpoint is removed.
    // Authentication is handled by Spring Authorization Server's /oauth2/token endpoint.
    private val userService: UserService,
    private val googleProfileDetailsService: GoogleProfileDetailsService,
    private val applicationConfig: ApplicationConfigProperties,
    private val authenticationManager: AuthenticationManager,
    private val tokenService: TokenService,
    // JWKSource removed as it's not directly used here; /oauth2/jwks endpoint handles it.
) {
    // Removed the custom @PostMapping("/login") endpoint.
    // Frontend should use POST /oauth2/token with grant_type=password.
    private val logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    @PostMapping("/login")
    fun login(
        @RequestBody requestBody: LoginRequest,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<UserAuthenticateResponse> {
        try {
            val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(requestBody.username, requestBody.password))
            userService.authenticateUser(requestBody.username, requestBody.password, authentication, request, response)
            return ResponseEntity.ok().body(UserAuthenticateResponse(tokenService.generateToken(authentication)))
        } catch (disabledError: DisabledException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (lockedError: LockedException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (badCredentialsError: BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @GetMapping("/login/oauth2/callback/google")
    fun oauth2LoginGoogle(
        @RequestParam("code") code: String,
        @RequestParam("scope") scope: String,
        @RequestParam("authuser") authUser: String,
        @RequestParam("prompt") prompt: String,
    ): ResponseEntity<*> { // Return type changed for clarity
        // TODO: Implement Google OAuth2 login flow properly
        // This typically involves exchanging the code for tokens, fetching user details,
        // finding or creating a local user, and generating a session/token for your application.
        return try {
            val accessToken = googleProfileDetailsService.oAuthAccessToken(code)
            val details = googleProfileDetailsService.getUserDetails(accessToken)

            val existingUser = userService.findByEmail(details.email)

            if (existingUser.isPresent) {
                println("USER EXISTS, CONNECT TO GOOGLE ACCOUNT")
                // TODO: Link existing account or log the user in (e.g., generate JWT or redirect)
                // For now, just return the user info
                ResponseEntity.ok(existingUser.get())
            } else {
                println("USER NOT EXISTS, CREATE NEW ACCOUNT!")
                // TODO: Create a new user based on Google profile details
                // Potentially redirect to a registration completion page or create directly
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found, registration required.")
            }
            // TODO: Redirect user or return appropriate response (e.g., JWT token)
        } catch (e: Exception) {
            println("Error during Google OAuth callback: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google OAuth failed.")
        }
    }

    @PostMapping("/login/oauth2/callback/github")
    fun oauth2LoginGitHub(@RequestParam oAuthId: String): ResponseEntity<*> {
        // TODO: Implement GitHub OAuth2 login flow
        println("GitHub callback received for oAuthId: $oAuthId")
        // Similar logic as Google: exchange code/token, get user info, find/create user, return token/redirect
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("GitHub login not implemented yet.")
    }

    // The public key endpoint is automatically exposed at /oauth2/jwks by Spring Authorization Server.
    // No need for a manual endpoint.

    @PostMapping("/register")
    fun register(@RequestBody request: UserCreateRequest): ResponseEntity<UserDto> {
        return try {
            // Check if user already exists (UserService.createUser handles this check too, but explicit check can give clearer error)
            if (userService.findByEmail(request.email).isPresent) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "User with email ${request.email} already exists.")
            }
            val newUser = userService.createUser(request)
            // Return 201 Created status with the created user DTO (without password)
            ResponseEntity.status(HttpStatus.CREATED).body(newUser)
        } catch (e: EntityExistsException) { // Catch specific exception if UserService throws it
            throw ResponseStatusException(HttpStatus.CONFLICT, "User with email ${request.email} already exists.", e)
        } catch (e: ResponseStatusException) {
            // Re-throw exceptions that already have a status
            throw e
        } catch (e: Exception) {
            // Log the error appropriately
            println("Error during registration: ${e.message}")
            // Return a generic server error
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Registration failed due to an internal error.", e
            )
        }
    }

    // Removed commented-out /secure endpoint. Access control should be handled by SecurityConfig.

}
