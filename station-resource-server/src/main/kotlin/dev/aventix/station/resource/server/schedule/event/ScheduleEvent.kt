package dev.aventix.station.resource.server.schedule.event

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
class ScheduleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    lateinit var id: UUID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    lateinit var eventType: ScheduleEventType

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "_timestamp", nullable = false)
    lateinit var timestamp: Instant

}