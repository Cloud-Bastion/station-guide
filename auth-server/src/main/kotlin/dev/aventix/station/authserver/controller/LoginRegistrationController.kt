package dev.aventix.station.authserver.controller

import dev.aventix.station.authserver.authenticate.LoginRequest
import dev.aventix.station.authserver.authenticate.TokenService
import dev.aventix.station.authserver.user.UserAuthenticateResponse
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
) {

    @PostMapping("/login")
    fun login(@RequestBody requestBody: LoginRequest): ResponseEntity<UserAuthenticateResponse> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                requestBody.username, requestBody.password
            )
        )

        try {
            return ResponseEntity.ok(UserAuthenticateResponse(tokenService.generateToken(authentication)))
        } catch (error: Error) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UserAuthenticateResponse(null, error))
        }
    }

    @PostMapping("/login/oauth2/callback/github")
    fun oauth2LoginGitHub(@RequestParam oAuthId: String) {

    }

    @PostMapping("/register")
    fun register() {

    }

    @PostMapping("/secure")
    fun secure(): String {
        return "very secure"
    }

}