package dev.aventix.station.resource.server.employee

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmployeeRepository : JpaRepository<EmployeeEntity, UUID> {
}