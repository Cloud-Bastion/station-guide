package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.StationTaskRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class StationScheduledTaskService(
    private val scheduledTaskRepository: StationScheduledTaskRepository,
    private val taskRepository: StationTaskRepository // Inject the task repository
) {

    fun create(request: StationScheduledTaskCreateRequest): StationScheduledTaskDTO {
        val subtasks = request.subtaskIds.map { taskId ->
            taskRepository.findById(taskId)
                .orElseThrow { EntityNotFoundException("Task with ID $taskId not found") }
        }

        val templateTask = request.templateTaskId?.let {
            taskRepository.findById(it)
                .orElseThrow { EntityNotFoundException("Template task with ID $it not found") }
        }

        val entity = StationScheduledTaskEntity().apply {
            this.permissionGroup = request.permissionGroup
            this.startTime = request.startTime
            this.endTime = request.endTime
            this.schedule = request.schedule
            this.title = request.title
            this.description = request.description
            this.subtasks = subtasks.toMutableList() // Use the retrieved subtasks
            this.files = request.files
            this.priority = request.priority
            this.createdBy = request.createdBy
            this.templateTask = templateTask
        }

        return scheduledTaskRepository.saveAndFlush(entity).toDto()
    }
}
