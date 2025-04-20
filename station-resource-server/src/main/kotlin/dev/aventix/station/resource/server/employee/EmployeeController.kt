package dev.aventix.station.resource.server.employee

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
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
//        println("Create employee request. Extracting access token...")
//        val token = http.getHeader("Authorization").substring("Bearer ".length)
//        println("token is: $token")
//        val restTemplate = RestTemplate()
//        val uri = "http://localhost:8080/api/v1/auth/create-account"
//        val headers = HttpHeaders().apply {
//            setBearerAuth(token)
//            add("client_id", "station-frontend")
//            add("grant_type", "code")
//            add("scope", "profile,openid")
//            accept = listOf(MediaType.APPLICATION_JSON)
//        }
//        val entity = HttpEntity(request, headers)
//        println("send request to auth server")
//        val result = restTemplate.exchange(uri, HttpMethod.POST, entity, String::class.java)

            employeeService.create(request)
        return ResponseEntity.ok().build()
//        return if (result.statusCode == HttpStatus.OK) {
//            ResponseEntity.ok().build()
//        } else {
//            ResponseEntity.status(result.statusCode).body(result.body)
//        }
    }

}