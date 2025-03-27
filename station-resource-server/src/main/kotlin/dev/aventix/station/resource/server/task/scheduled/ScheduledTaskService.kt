package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.TaskDTO
import dev.aventix.station.resource.server.task.TaskEntity
import dev.aventix.station.resource.server.task.TaskRepository
import dev.aventix.station.resource.server.task.TaskService
import dev.aventix.station.resource.server.task.scheduled.request.ScheduledTaskCreateRequest
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class ScheduledTaskService(
    private val scheduledTaskRepository: ScheduledTaskRepository,
    private val taskRepository: TaskRepository,
    private val testService: TaskService,
) {
    @PostConstruct
    fun init() {
        this.create(
            ScheduledTaskCreateRequest(
                TaskDTO(
                    null, "Test-Titel", "Test-Beschreibung", 2, null, null, null, null, false, true
                ), "WEEKLY", listOf(1, 4), listOf(), LocalTime.MIN, null, 0, null
            )
        )
        this.testService.checkTask()
    }

    fun create(request: ScheduledTaskCreateRequest): ScheduledTaskDTO {
        return this.scheduledTaskRepository.saveAndFlush(ScheduledTaskEntity().apply {
            this.template = TaskEntity().apply {
                this.title = request.template.title
                request.template.description?.let { this.description = it }
                this.priority = request.template.priority ?: 0
                request.template.permissionGroup?.let { this.permissionGroup = it }
                request.template.createdBy?.let { this.createdBy = it }
                this.startTime = null
                this.endTime = null
                this.completed = false
                this.isTemplate = true
            }
            this.frequency = request.frequency
            this.daysOfWeek = request.daysOfWeek ?: listOf()
            this.daysOfMonth = request.daysOfMonth ?: listOf()
            this.startTime = request.startTime ?: LocalTime.MIN
            this.endTime = request.endTime
            this.endTimeDaysAdd = request.endTimeDaysAdd ?: 0
            this.lastCreatedTask = null
        }).toDTO()
    }
}
