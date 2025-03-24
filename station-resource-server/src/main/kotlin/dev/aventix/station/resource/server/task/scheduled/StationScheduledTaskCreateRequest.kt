package dev.aventix.station.resource.server.task.scheduled

import java.time.Instant
import java.util.UUID

data class StationScheduledTaskCreateRequest(
    val permissionGroup: String?,
    val startTime: Instant?,
    val endTime: Instant?,
    val schedule: String?,
    val title: String?,
    val description: String?,
    val subtaskIds: List<UUID>, // IDs of existing tasks to use as subtasks
    val files: List<String>,
    val priority: Int,
    val createdBy: String?,
    val templateTaskId: UUID?
)
