package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.StationTaskRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class StationScheduledTaskService(
    private val scheduledTaskRepository: StationScheduledTaskRepository,
    private val taskRepository: StationTaskRepository
) {

    fun create(request: StationScheduledTaskCreateRequest): StationScheduledTaskDTO {
        val templateTask = request.templateTaskId?.let {
            taskRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Template task with ID $it not found") }
        }

        val entity = StationScheduledTaskEntity().apply {
            this.permissionGroup = request.permissionGroup
            this.startTime = request.startTime
            this.endTime = request.endTime
            this.recurring = request.recurring
            this.recurrenceRule = request.recurrenceRule
            this.title = request.title
            this.description = request.description
            this.files = request.files
            this.priority = request.priority
            this.createdBy = request.createdBy
            this.templateTask = templateTask
        }

        return scheduledTaskRepository.saveAndFlush(entity).toDto()
    }
}
