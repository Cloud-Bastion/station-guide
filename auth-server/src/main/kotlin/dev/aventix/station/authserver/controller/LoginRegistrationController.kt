package dev.aventix.station.authserver.controller

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import dev.aventix.station.authserver.authenticate.LoginRequest
import dev.aventix.station.authserver.authenticate.TokenService
import dev.aventix.station.authserver.config.ApplicationConfigProperties
import dev.aventix.station.authserver.provider.google.GoogleProfileDetailsService
import dev.aventix.station.authserver.user.UserAuthenticateResponse
import dev.aventix.station.authserver.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.Throws

@RestController
@RequestMapping("/api/v1/auth")
class LoginRegistrationController(
    private val authenticationManager: AuthenticationManager,
    private val tokenService: TokenService,
    private val userService: UserService,
    private val googleProfileDetailsService: GoogleProfileDetailsService,
    private val applicationConfig: ApplicationConfigProperties,
    // JWKSource is injected but the endpoint to expose keys is handled by Spring Authorization Server automatically
    private val jwkSource: JWKSource<SecurityContext>,
) {

    @PostMapping("/login")
    fun login(@RequestBody requestBody: LoginRequest): ResponseEntity<UserAuthenticateResponse> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                requestBody.username, requestBody.password
            )
        )

        return try {
            ResponseEntity.ok(UserAuthenticateResponse(tokenService.generateToken(authentication)))
        } catch (error: Exception) { // Catch broader exception if needed
            // Log the error appropriately
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticateResponse(null, Error("Authentication failed"))) // Provide a generic error
        }
    }

    @GetMapping("/login/oauth2/callback/google")
    fun oauth2LoginGoogle(@RequestParam("code") code: String,
                          @RequestParam("scope") scope: String,
                          @RequestParam("authuser") authUser: String,
                          @RequestParam("prompt") prompt: String): Unit {
        // TODO: Implement Google OAuth2 login flow properly
        // This typically involves exchanging the code for tokens, fetching user details,
        // finding or creating a local user, and generating a session/token for your application.
        val accessToken = googleProfileDetailsService.oAuthAccessToken(code)
        val details = googleProfileDetailsService.getUserDetails(accessToken)

        if (userService.findByEmail(details.email).isPresent) {
            println("USER EXISTS, CONNECT TO GOOGLE ACCOUNT")
            // TODO: Link existing account or log the user in
        } else {
            println("USER NOT EXISTS, CREATE NEW ACCOUNT!")
            // TODO: Create a new user based on Google profile details
        }
        // TODO: Redirect user or return appropriate response (e.g., JWT token)
    }

    @PostMapping("/login/oauth2/callback/github")
    fun oauth2LoginGitHub(@RequestParam oAuthId: String) {
        // TODO: Implement GitHub OAuth2 login flow
    }

    // The public key endpoint is automatically exposed at /oauth2/jwks by Spring Authorization Server
    // No need for a manual endpoint like the commented out one below.
    /*@GetMapping("/jwt/public-key")
    fun exposePublicKey(): ResponseEntity<Map<String, Any>> {
         // This manual implementation is not needed.
         // Use the JWKSource bean if you need programmatic access to keys.
         // The endpoint /oauth2/jwks serves the keys automatically.
         return ResponseEntity.ok(mapOf("keys" to ... )) // Example structure
    }*/

    @PostMapping("/register")
    fun register() {
        // TODO: Implement user registration logic
    }

    // This endpoint seems unnecessary if security is handled correctly via Spring Security filters
    /*@PostMapping("/secure")
    fun secure(): String {
        return "very secure"
    }*/

}
