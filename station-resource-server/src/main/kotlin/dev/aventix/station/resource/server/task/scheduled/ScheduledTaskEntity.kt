package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.TaskEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Entity
@Table(name = "station_task_scheduled")
class ScheduledTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @OneToOne(cascade = [(CascadeType.ALL)], orphanRemoval = true)
    @JoinColumn(name = "template_id")
    lateinit var template: TaskEntity

    lateinit var frequency: String//DAILY, WEEKLY, MONTHLY

    //TODO: Maybe converter von string?
    @ElementCollection(fetch = FetchType.EAGER)
    var daysOfWeek: List<Int> = listOf()//1 for monday, 2 for thursday ...

    //TODO: Maybe converter von string?
    @ElementCollection(fetch = FetchType.EAGER)
    var daysOfMonth: List<Int> = listOf()//1 for first of month 15 for 15 of month...32 for last day of month

    var startTime: LocalTime = LocalTime.MIN

    var endTime: LocalTime? = null
    var endTimeDaysAdd: Int = 0
    var lastCreatedTask: LocalDateTime? = null

    fun toDTO(): ScheduledTaskDTO {
        return ScheduledTaskDTO(
            this.id,
            this.template.toDTO(),
            this.frequency,
            this.daysOfWeek,
            this.daysOfMonth,
            this.startTime,
            this.endTime,
            this.endTimeDaysAdd,
            this.lastCreatedTask
        )
    }
}
