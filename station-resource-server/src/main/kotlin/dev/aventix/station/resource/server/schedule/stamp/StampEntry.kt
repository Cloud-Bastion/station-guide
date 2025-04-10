package dev.aventix.station.resource.server.schedule.stamp

import dev.aventix.station.resource.server.employee.EmployeeEntity
import jakarta.persistence.*
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

@Entity
class StampEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    lateinit var id: UUID

    @ManyToOne(cascade = [(CascadeType.MERGE)], fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    lateinit var assignee: EmployeeEntity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    lateinit var eventType: StampEntryType

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "_timestamp", nullable = false)
    lateinit var timestamp: OffsetDateTime

    @Column(nullable = true)
    lateinit var note: String

    var edited: Boolean = false

    @ManyToOne(cascade = [(CascadeType.MERGE)], fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_id")
    var createdBy: EmployeeEntity? = null

    fun toDto(): StampEntryDto {
        return StampEntryDto(
            id, assignee.id, timestamp.atZoneSameInstant(ZoneId.of("Europe/Berlin")), eventType
        )
    }

}