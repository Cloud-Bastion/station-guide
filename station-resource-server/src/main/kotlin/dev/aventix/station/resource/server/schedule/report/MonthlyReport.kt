package dev.aventix.station.resource.server.schedule.report

import dev.aventix.station.resource.server.employee.EmployeeEntity
import dev.aventix.station.resource.server.schedule.report.bonus.PaidBonusEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "monthly_reports")
class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    var year: Int = 0

    var month: Int = 0

    @ManyToOne(fetch = FetchType.EAGER)
    lateinit var employee: EmployeeEntity

    var totalWorkHours: Int = 0
    var totalWorkMinutes: Int = 0
    var totalWorkSeconds: Int = 0

    var paidOverTimeHours: Int = 0

    @ElementCollection
    @CollectionTable(name = "employee_bonus_map", joinColumns = [JoinColumn(name = "bonus_id")])
    @MapKeyColumn(name = "bonus_type")
    @Column(name = "hours")
    var bonus: MutableMap<PaidBonusEntity, Double> = mutableMapOf()

    fun toDto(): MonthlyReportDto {
        return MonthlyReportDto(
            id,
            year,
            month,
            employee.toDTO(),
            totalWorkHours,
            totalWorkMinutes,
            totalWorkSeconds,
            paidOverTimeHours,
            bonus.mapKeys { (bonusEntity) -> bonusEntity.toDto() }
        )
    }

}