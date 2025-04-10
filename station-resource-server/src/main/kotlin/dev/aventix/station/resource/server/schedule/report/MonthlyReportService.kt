package dev.aventix.station.resource.server.schedule.report

import dev.aventix.station.resource.server.employee.EmployeeEntity
import dev.aventix.station.resource.server.employee.EmployeeService
import dev.aventix.station.resource.server.schedule.stamp.StampEntryService
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class MonthlyReportService(
    private val reportRepository: MonthlyReportRepository,
    private val employeeService: EmployeeService,
    private val stampService: StampEntryService,
) {

    fun create(request: MonthlyReportCreateRequest) {


    // run for all employees
    }

    @Throws(EntityNotFoundException::class)
    fun createForEmployee(year: Int, month: Int, employeeId: UUID) {
        val employee = employeeService.findById(employeeId).orElseThrow { EntityNotFoundException("Employee does not exist.") }
        val daysInMonth = Calendar.getInstance().getActualMaximum(month + 1);
        val startDate = LocalDate.of(year, month, 1)
        val endDate = LocalDate.of(year, month, daysInMonth)

        val stamps = stampService.getStampsInRange(startDate, endDate, employee)

    }



}