package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.StationTaskDTO
import java.time.Instant
import java.util.UUID

data class StationScheduledTaskDTO(
    val id: UUID,
    val permissionGroup: String?,
    val startTime: Instant?,
    val endTime: Instant?,
    val schedule: String?,
    val title: String?,
    val description: String?,
    val subtasks: List<StationTaskDTO>,
    val files: List<String>,
    val priority: Int,
    val createdBy: String?,
    val completed: Boolean,
    val templateTaskId: UUID?
)
