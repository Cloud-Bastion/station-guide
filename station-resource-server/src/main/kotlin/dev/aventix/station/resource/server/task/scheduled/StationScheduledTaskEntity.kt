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

    //Start time
    @Temporal(TemporalType.TIMESTAMP)
    var startTime: Instant? = null

    //End time
    @Temporal(TemporalType.TIMESTAMP)
    var endTime: Instant? = null

    //scheduled day,week, month
    var schedule: String? = null // Could be a cron expression or a custom format

    //title
    var title: String? = null

    //description
    var description: String? = null

    //sublist <Tasks>
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "scheduled_task_id")
    var subtasks: MutableList<StationTaskEntity> = mutableListOf()

    //file list to download
    // Assuming a simple list of file URLs.  Could be a more complex type.
    @ElementCollection
    var files: List<String> = listOf()

    //priority
    var priority: Int = 0 // Or an enum

    //created by
    var createdBy: String? = null // Or a User entity

    //completed
    var completed: Boolean = false

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
            schedule = this.schedule,
            title = this.title,
            description = this.description,
            subtasks = this.subtasks.map { it.toDto() },
            files = this.files,
            priority = this.priority,
            createdBy = this.createdBy,
            completed = this.completed,
            templateTaskId = this.templateTask?.id
        )
    }
}
