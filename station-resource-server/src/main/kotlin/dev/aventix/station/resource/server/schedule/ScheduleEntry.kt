package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeEntity
import dev.aventix.station.resource.server.schedule.event.ScheduleEvent
import jakarta.persistence.*
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

    @OneToMany(cascade = [(CascadeType.ALL)],
        fetch = FetchType.EAGER,
        orphanRemoval = true)
    @JoinColumn(name = "schedule_entry_id")
    lateinit var events: MutableList<ScheduleEvent>

}