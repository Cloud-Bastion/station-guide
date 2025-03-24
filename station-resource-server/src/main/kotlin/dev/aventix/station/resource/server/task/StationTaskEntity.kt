package dev.aventix.station.resource.server.task

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "station_task")
class StationTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    lateinit var id: UUID

    //Permission group defined
    var permissionGroup: String? = null // Or a more complex type if needed

    //Time to end
    @Temporal(TemporalType.TIMESTAMP)
    var endTime: Instant? = null

    //is template?
    var isTemplate: Boolean = false

    fun toDto(): StationTaskDTO {
        return StationTaskDTO(
            id = this.id,
            permissionGroup = this.permissionGroup,
            endTime = this.endTime,
            isTemplate = this.isTemplate
        )
    }
}
