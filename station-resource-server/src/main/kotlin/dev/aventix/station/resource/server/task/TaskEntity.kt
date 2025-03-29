package dev.aventix.station.resource.server.task

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "station_task")
class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @Column(nullable = false, unique = false)
    lateinit var title: String

    @Column(columnDefinition = "TEXT") // Explizit TEXT für längere Beschreibungen mit Zeilenumbrüchen
    var description: String? = null

    var priority: Int = 0

    /*TODO: add files@ElementCollection
    private val files: List<String> = listOf()*/
    //TODO: Add sub task list

    var permissionGroup: String? = null
    var createdBy: String? = null

    var startTime: LocalDateTime? = null
    var endTime: LocalDateTime? = null
    var completed: Boolean = false
    var isTemplate: Boolean = false

    fun toDTO(): TaskDTO {
        return TaskDTO(
            this.id,
            this.title,
            this.description,
            this.priority,
            this.permissionGroup,
            this.createdBy,
            this.startTime,
            this.endTime,
            this.completed,
            this.isTemplate
        )
    }
}
