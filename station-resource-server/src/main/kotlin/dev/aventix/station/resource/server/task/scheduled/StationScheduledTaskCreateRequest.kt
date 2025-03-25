package dev.aventix.station.resource.server.task.scheduled

import java.time.Instant
import java.util.UUID

data class StationScheduledTaskCreateRequest(
    val permissionGroup: String?,
    val startTime: Instant?,
    val endTime: Instant?,
    val recurring: Boolean,
    val recurrenceRule: String?,
    val title: String?,
    val description: String?,
    val files: List<String>,
    val priority: Int,
    val createdBy: String?,
    val templateTaskId: UUID?
)
