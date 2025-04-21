package dev.aventix.station.resource.server.employee

import dev.aventix.station.resource.server.employee.address.EmployeeAddressDTO
import dev.aventix.station.resource.server.employee.address.EmployeeAddressEntity
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityExistsException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import kotlin.NoSuchElementException
import kotlin.jvm.Throws
import kotlin.jvm.optionals.getOrNull

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
) {
    fun findById(id: UUID): Optional<EmployeeEntity> = employeeRepository.findById(id)

    @Throws(EntityExistsException::class, IllegalArgumentException::class)
    fun create(createRequest: EmployeeCreateRequest): EmployeeDTO {
        return this.employeeRepository.saveAndFlush(
            EmployeeEntity().apply { ->
                this.badgeNumber = createBadgeNumber()
                this.firstName = createRequest.firstName
                this.lastName = createRequest.lastName
                this.email = createRequest.email
                this.birthDate = createRequest.birthDate
                this.address = createRequest.address.let {
                    EmployeeAddressEntity().apply {
                        zipcode = it.zipcode as Integer
                        city = it.city
                        street = it.street
                        streetNumber = it.streetNumber
                        apartment = it.apartment
                    }
                }
                this.taxId = createRequest.taxId
                this.socialSecurityId = createRequest.socialSecurityId
                createRequest.createdBy?.let {
                    val employee = employeeRepository.findById(it.id)
                    this.createdBy = employee.getOrNull()
                }
            }).toDTO()
    }

    @Throws(NoSuchElementException::class)
    fun patch(patchRequest: EmployeePatchRequest): EmployeeDTO {
        val entity = this.employeeRepository.findById(patchRequest.employeeId).getOrNull()
            ?: throw NoSuchElementException("No employee with id ${patchRequest.employeeId}")

        patchRequest.firstName?.let { entity.firstName = it }
        patchRequest.lastName?.let { entity.lastName = it }
        patchRequest.email?.let { entity.email = it }
        patchRequest.address?.let {
            entity.address = EmployeeAddressEntity().apply {
                zipcode = it.zipcode as Integer
                city = it.city
                street = it.street
                streetNumber = it.streetNumber
                apartment = it.apartment
            }
        }
        patchRequest.taxId?.let { entity.taxId = it }
        patchRequest.socialSecurityId?.let { entity.socialSecurityId = it }
        return this.employeeRepository.saveAndFlush(entity).toDTO()
    }

    fun getAllEmployees(): List<EmployeeDTO> =
        employeeRepository.findAll().map { it.toDTO() }

    private fun createBadgeNumber(): Long {
        return this.employeeRepository.count() + 1
    }
}