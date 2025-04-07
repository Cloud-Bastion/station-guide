package dev.aventix.station.resource.server.schedule.report

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MonthlyReportRepository: JpaRepository<MonthlyReport, UUID> {
}