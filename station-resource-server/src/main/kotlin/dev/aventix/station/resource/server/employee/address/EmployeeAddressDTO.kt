package dev.aventix.station.resource.server.employee.address

data class EmployeeAddressDTO(
    var zipcode: Int,
    var city: String,
    var street: String,
    var streetNumber: String,
    var apartment: String?
)