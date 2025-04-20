package dev.aventix.station.authserver.controller

import dev.aventix.station.authserver.user.UserCreateRequest
import dev.aventix.station.authserver.user.UserService
import jakarta.persistence.EntityExistsException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/create-account")
    fun createAccount(@RequestBody request: UserCreateRequest): ResponseEntity<Any> {
        try {
            val user = userService.createUser(request)
            return ResponseEntity.ok(user)
        } catch (e: EntityExistsException) {
            return ResponseEntity.badRequest().build()
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("")
        }
    }

}