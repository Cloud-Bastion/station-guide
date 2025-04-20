package dev.aventix.station.resource.server.employee

import dev.aventix.station.resource.server.employee.address.EmployeeAddressDTO
import dev.aventix.station.resource.server.employee.address.EmployeeAddressEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class EmployeeCreateRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val address: EmployeeAddressDTO,
    val birthDate: LocalDate,
    val taxId: Long,
    val socialSecurityId: String,
    val createdBy: EmployeeDTO?,
    val createdAt: LocalDateTime? = LocalDateTime.now()
)
