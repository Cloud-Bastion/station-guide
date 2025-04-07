package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
class PlannedScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    lateinit var id: UUID

    @ManyToOne
    @JoinColumn(name = "assignee_id", nullable = false)
    lateinit var assignee: EmployeeEntity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    lateinit var entryType: PlannedScheduleEntryType

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    lateinit var startTime: LocalDateTime

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    lateinit var endTime: LocalDateTime

    var breakMinutes: Int = 0

}