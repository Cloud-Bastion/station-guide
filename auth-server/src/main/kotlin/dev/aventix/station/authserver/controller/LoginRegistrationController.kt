package dev.aventix.station.authserver.controller

import dev.aventix.station.authserver.authenticate.LoginRequest
import dev.aventix.station.authserver.authenticate.TokenService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth/")
class LoginRegistrationController(
    private val authenticationManager: AuthenticationManager,
    private val tokenService: TokenService
) {

    @PostMapping("/login")
    fun login(@RequestBody requestBody: LoginRequest): String {
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(requestBody.username, requestBody.password))
        return tokenService.generateToken(authentication)
    }

    @PostMapping("/register")
    fun register() {

    }

    @PostMapping("/secure")
    fun secure(): String {
        return "very secure"
    }

}