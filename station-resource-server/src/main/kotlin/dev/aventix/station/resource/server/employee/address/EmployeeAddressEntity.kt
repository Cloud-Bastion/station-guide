package dev.aventix.station.resource.server.employee.address

import jakarta.persistence.*

@Embeddable
class EmployeeAddressEntity {
    @Column(nullable = false)
    lateinit var zipcode: Integer

    @Column(nullable = false)
    lateinit var city: String

    @Column(nullable = false)
    lateinit var street: String

    @Column(nullable = false)
    lateinit var streetNumber: String

    @Column(nullable = true)
    var apartment: String? = null

    fun toDTO(): EmployeeAddressDTO {
        return EmployeeAddressDTO(
            this.zipcode.toInt(),
            this.city,
            this.street,
            this.streetNumber,
            this.apartment
        )
    }
}