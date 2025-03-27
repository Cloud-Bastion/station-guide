package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.TaskEntity
import dev.aventix.station.resource.server.task.TaskService
import dev.aventix.station.resource.server.task.scheduled.request.ScheduledTaskCreateRequest
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class ScheduledTaskService(
    private val scheduledTaskRepository: ScheduledTaskRepository,
    private val taskService: TaskService,
) {
    fun create(request: ScheduledTaskCreateRequest): ScheduledTaskDTO {
        val dto = this.scheduledTaskRepository.saveAndFlush(ScheduledTaskEntity().apply {
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
        this.taskService.checkTask()
        return dto
    }
}
