package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.StationTaskEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "station_scheduled_task")
class StationScheduledTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    lateinit var id: UUID

    //Permission group defined
    var permissionGroup: String? = null // Or a more complex type

    //Start time (for the first occurrence or the general schedule)
    @Temporal(TemporalType.TIMESTAMP)
    var startTime: Instant? = null

    //End time (optional, for tasks with a defined end date)
    @Temporal(TemporalType.TIMESTAMP)
    var endTime: Instant? = null

    //Indicates if the task is recurring
    var recurring: Boolean = false

    //Recurrence rule (e.g., "daily", "weekly", "monthly", or a CRON expression)
    var recurrenceRule: String? = null

    //title
    var title: String? = null

    //description
    var description: String? = null

    //file list to download
    // Assuming a simple list of file URLs.  Could be a more complex type.
    @ElementCollection
    var files: List<String> = listOf()

    //priority
    var priority: Int = 0 // Or an enum

    //created by
    var createdBy: String? = null // Or a User entity

    //use station task as template
    @ManyToOne
    @JoinColumn(name = "template_task_id")
    var templateTask: StationTaskEntity? = null

    fun toDto(): StationScheduledTaskDTO {
        return StationScheduledTaskDTO(
            id = this.id,
            permissionGroup = this.permissionGroup,
            startTime = this.startTime,
            endTime = this.endTime,
            recurring = this.recurring,
            recurrenceRule = this.recurrenceRule,
            title = this.title,
            description = this.description,
            files = this.files,
            priority = this.priority,
            createdBy = this.createdBy,
            templateTaskId = this.templateTask?.id
        )
    }
}
