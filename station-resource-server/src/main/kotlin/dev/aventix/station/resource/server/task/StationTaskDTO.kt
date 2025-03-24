package dev.aventix.station.resource.server.task

import java.time.Instant
import java.util.UUID

data class StationTaskDTO(
    val id: UUID,
    val permissionGroup: String?,
    val endTime: Instant?,
    val isTemplate: Boolean
)
