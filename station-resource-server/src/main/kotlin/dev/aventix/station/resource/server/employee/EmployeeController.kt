package dev.aventix.station.resource.server.employee

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/api/v1/employees")
class EmployeeController(
    private val employeeService: EmployeeService,
) {

    @PostMapping("/create")
    //  @PreAuthorize("hasAuthority('SCOPE_user.create')")
    fun createEmployee(@RequestBody request: EmployeeCreateRequest, http: HttpServletRequest): ResponseEntity<Any> {
        employeeService.create(request)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getAllEmployees(): ResponseEntity<List<EmployeeDTO>> {
        return ResponseEntity.ok(employeeService.getAllEmployees())
    }

}