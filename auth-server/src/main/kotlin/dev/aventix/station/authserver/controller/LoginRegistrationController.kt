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
        } catch (error: Error) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticateResponse(null, error))
        }
    }

    @GetMapping("/login/oauth2/callback/google")
    fun oauth2LoginGoogle(@RequestParam("code") code: String,
                          @RequestParam("scope") scope: String,
                          @RequestParam("authuser") authUser: String,
                          @RequestParam("prompt") prompt: String): Unit {
        val accessToken = googleProfileDetailsService.oAuthAccessToken(code)
        val details = googleProfileDetailsService.getUserDetails(accessToken)

        if (userService.findByEmail(details.email).isPresent) {
            println("USER EXISTS, CONNECT TO GOOGLE ACCOUNT")
        } else {
            println("USER NOT EXISTS, CREATE NEW ACCOUNT!")
        }
    }

    @PostMapping("/login/oauth2/callback/github")
    fun oauth2LoginGitHub(@RequestParam oAuthId: String) {

    }

    @GetMapping("/jwt/public-key")
    fun exposePublicKey(jwkSource: JWKSource<SecurityContext>): ResponseEntity<Map<String, List<JWK>>> {
        return ResponseEntity.ok(mapOf("keys" to jwkSource(null, null)))
    }

    @PostMapping("/register")
    fun register() {

    }

    @PostMapping("/secure")
    fun secure(): String {
        return "very secure"
    }

}