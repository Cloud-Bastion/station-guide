package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.user.authority.UserAuthorityDto
import dev.aventix.station.authserver.user.role.UserRoleCreateRequest
import dev.aventix.station.authserver.user.role.UserRoleDto
import dev.aventix.station.authserver.user.role.UserRoleService
import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth/role")
class UserRoleAuthorityController(
    private val userRoleService: UserRoleService,
) {
    @GetMapping
    fun getAllRoles(): ResponseEntity<List<UserRoleDto>> {
        return ResponseEntity.ok(this.userRoleService.getAllRoles())
    }

    @GetMapping("/{role-name}")
    fun getRoleByName(@PathVariable("role-name") name: String): ResponseEntity<UserRoleDto> {
        return try {
            ResponseEntity.ok(this.userRoleService.getRoleByName(name))
        } catch (notFound: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createRole(@RequestBody request: UserRoleCreateRequest): ResponseEntity<UserRoleDto> {
        return try {
            ResponseEntity.ok(
                this.userRoleService.createRole(request)
            )
        } catch (notFound: EntityExistsException) {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/add/{role-name}/{authority}")
    fun addAuthorityToRole(
        @PathVariable("role-name") name: String,
        @PathVariable("authority") authority: String,
    ): ResponseEntity<UserRoleDto> {
        return try {
            ResponseEntity.ok(this.userRoleService.addAuthorityToRole(name, authority))
        } catch (notFound: EntityExistsException) {
            ResponseEntity.notFound().build()
        } catch (notFound: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/remove/{role-name}/{authority}")
    fun removeAuthorityToRole(
        @PathVariable("role-name") name: String,
        @PathVariable("authority") authority: String,
    ): ResponseEntity<UserRoleDto> {
        return try {
            ResponseEntity.ok(this.userRoleService.removeAuthorityToRole(name, authority))
        } catch (notFound: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/set/{role-name}")
    fun setAuthoritiesToRole(
        @PathVariable("role-name") name: String,
        @RequestBody authorities: List<String>,
    ): ResponseEntity<UserRoleDto> {
        return try {
            ResponseEntity.ok(this.userRoleService.setAuthoritiesToRole(name, authorities))
        } catch (notFound: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}