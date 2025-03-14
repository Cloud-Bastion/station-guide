package dev.aventix.station.resource.server.employee

import dev.aventix.station.resource.server.employee.address.EmployeeAddressDTO
import dev.aventix.station.resource.server.employee.address.EmployeeAddressEntity
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityExistsException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.jvm.Throws
import kotlin.jvm.optionals.getOrNull

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository
) {
    //TODO: Delete after tests
    @PostConstruct
    fun init() {
        this.create(
            EmployeeCreateRequest(
                "Melvin", "Schneider", "melvinschneider02@gmail.com", EmployeeAddressDTO(
                    64342, "Seeheim-Jugenheim", "Fliederweg", "10-18", null
                ), LocalDate.now(), 412341243214L, "adwadawd232dawdw", null
            )
        )
    }

    @Throws(EntityExistsException::class, IllegalArgumentException::class)
    fun create(createRequest: EmployeeCreateRequest): EmployeeEntity {
        println("CREATING EMPLOYEE")
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
            })
    }

    @Throws(NoSuchElementException::class)
    fun update(patchRequest: EmployeePatchRequest): EmployeeEntity {
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
        return this.employeeRepository.saveAndFlush(entity)
    }

    private fun createBadgeNumber(): Long {
        return this.employeeRepository.count() + 1
    }
}