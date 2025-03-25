package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.StationTaskEntity
import dev.aventix.station.resource.server.task.StationTaskRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StationScheduledTaskService(
    private val scheduledTaskRepository: StationScheduledTaskRepository,
    private val taskRepository: StationTaskRepository
) {

    fun create(request: StationScheduledTaskCreateRequest): StationScheduledTaskDTO {
        // Create the template task first
        val templateTask = StationTaskEntity().apply {
            this.permissionGroup = request.permissionGroup
            this.endTime = request.endTime
            this.isTemplate = true // Mark as a template
            this.scheduledTask = null // Initially not associated with a scheduled task
            this.completed = false;
        }
        val savedTemplateTask = taskRepository.save(templateTask)


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
            this.templateTask = savedTemplateTask // Associate with the created template task
        }

        val savedScheduledTask = scheduledTaskRepository.saveAndFlush(entity)

        //now update the template task with the scheduled task
        savedTemplateTask.scheduledTask = savedScheduledTask
        taskRepository.save(savedTemplateTask)

        return savedScheduledTask.toDto()
    }
}
