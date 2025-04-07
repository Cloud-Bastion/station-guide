package dev.aventix.station.resource.server.schedule.report

import dev.aventix.station.resource.server.employee.EmployeeDTO
import dev.aventix.station.resource.server.schedule.report.bonus.PaidBonusDto
import java.util.UUID

data class MonthlyReportDto(
    val id: UUID,
    val year: Int,
    val month: Int,
    val employee: EmployeeDTO,
    val workHours: Int,
    val workMinutes: Int,
    val workSeconds: Int,
    val paidOvertimeHours: Int,
    val paidBonus: Map<PaidBonusDto, Double>,
)