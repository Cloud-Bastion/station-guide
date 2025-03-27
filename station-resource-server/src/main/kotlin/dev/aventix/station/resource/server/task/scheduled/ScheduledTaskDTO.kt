package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.TaskDTO
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

data class ScheduledTaskDTO(
    val id: UUID,
    val template: TaskDTO,
    val frequency: String,
    val daysOfWeek: List<Int>? = listOf(),
    val daysOfMonth: List<Int>? = listOf(),
    val startTime: LocalTime? = LocalTime.MIN,
    val endTime: LocalTime? = null,
    val endTimeDaysAdd: Int? = null,
    val lastCreatedTask: LocalDateTime? = null,
)