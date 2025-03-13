package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
class ScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    lateinit var id: UUID

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    lateinit var assignee: EmployeeEntity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    lateinit var planType: ScheduleEntryPlanType

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    lateinit var entryType: ScheduleEntryType

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    lateinit var startDate: Instant

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    lateinit var endDate: Instant


}