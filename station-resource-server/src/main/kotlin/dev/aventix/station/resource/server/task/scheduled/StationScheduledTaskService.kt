package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.StationTaskEntity
import dev.aventix.station.resource.server.task.StationTaskRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class StationScheduledTaskService(
    private val scheduledTaskRepository: StationScheduledTaskRepository,
    private val taskRepository: StationTaskRepository
) {

    @Transactional // Ensure all operations happen within a single transaction
    fun create(request: StationScheduledTaskCreateRequest): StationScheduledTaskDTO {
        // 1. Create instances
        val templateTask = StationTaskEntity().apply {
            this.permissionGroup = request.permissionGroup
            this.endTime = request.endTime
            this.isTemplate = true // Mark as a template
            this.completed = false
            // scheduledTask will be set below
        }

        val scheduledTask = StationScheduledTaskEntity().apply {
            this.permissionGroup = request.permissionGroup
            this.startTime = request.startTime
            this.endTime = request.endTime
            this.recurring = request.recurring
            this.recurrenceRule = request.recurrenceRule
            this.title = request.title
            this.description = request.description
            this.files = request.files ?: listOf()
            this.priority = request.priority
            this.createdBy = request.createdBy
            // templateTask will be set below
        }

        // 2. Set both sides of the relationship before saving
        templateTask.scheduledTask = scheduledTask
        scheduledTask.templateTask = templateTask

        // 3. Save templateTask first using save()
        // The ID will be generated and managed within the persistence context.
        val savedTemplateTask = taskRepository.save(templateTask)

        // 4. Save scheduledTask second using save().
        // Hibernate should handle the relationship persistence within the transaction.
        val savedScheduledTask = scheduledTaskRepository.save(scheduledTask)

        // The relationship should now be correctly persisted in both tables
        // when the transaction commits.

        return savedScheduledTask.toDto()
    }
}
