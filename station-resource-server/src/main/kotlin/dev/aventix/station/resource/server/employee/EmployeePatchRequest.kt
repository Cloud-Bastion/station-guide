package dev.aventix.station.resource.server.employee

import dev.aventix.station.resource.server.employee.address.EmployeeAddressDTO
import dev.aventix.station.resource.server.employee.address.EmployeeAddressEntity
import java.util.UUID

data class EmployeePatchRequest(
    val employeeId: UUID,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val address: EmployeeAddressDTO?,
    val taxId: Long?,
    val socialSecurityId: String?,
)
