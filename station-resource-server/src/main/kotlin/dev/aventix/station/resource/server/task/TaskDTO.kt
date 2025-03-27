package dev.aventix.station.resource.server.task

import java.time.LocalDateTime
import java.util.*

data class TaskDTO(
    val id: UUID? = null,
    val title: String,
    val description: String? = null,
    val priority: Int? = 0,
    val permissionGroup: String? = null,
    val createdBy: String? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val completed: Boolean = false,
    val isTemplate: Boolean = false,
)