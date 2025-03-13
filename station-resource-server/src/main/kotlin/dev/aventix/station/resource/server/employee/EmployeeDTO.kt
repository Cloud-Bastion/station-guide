package dev.aventix.station.resource.server.employee

import dev.aventix.station.resource.server.employee.address.EmployeeAddressDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class EmployeeDTO(
    val id: UUID,
    val badgeNumber: Int?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: EmployeeAddressDTO,
    val birthDate: LocalDate,
    val taxId: Long,
    val socialSecurityId: String,
    val createdAt: LocalDateTime?,
    val createdBy: EmployeeDTO?
)