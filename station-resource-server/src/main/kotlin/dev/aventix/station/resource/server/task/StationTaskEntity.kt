package dev.aventix.station.resource.server.task

import dev.aventix.station.resource.server.task.scheduled.StationScheduledTaskEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "station_task")
class StationTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID.randomUUID()

    var permissionGroup: String? = null
    var endTime: Instant? = null
    var isTemplate: Boolean = false

    @ManyToOne
    @JoinColumn(name = "scheduled_task_id")
    var scheduledTask: StationScheduledTaskEntity? = null

    var completed: Boolean = false

    fun toDto(): StationTaskDTO {
        return StationTaskDTO(
            id = this.id,
            permissionGroup = this.permissionGroup,
            endTime = this.endTime,
            isTemplate = this.isTemplate,
            scheduledTaskId = this.scheduledTask?.id,
            completed = this.completed
        )
    }
}
