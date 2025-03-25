package dev.aventix.station.resource.server.task

import org.springframework.stereotype.Service

@Service
class StationTaskService(private val taskRepository: StationTaskRepository) {

    fun findOpenPlannedTasks(): List<StationTaskDTO> {
        return taskRepository.findOpenPlannedTasks().map { it.toDto() }
    }

    // New method to find ALL open tasks
    fun findAllOpenTasks(): List<StationTaskDTO> {
        return taskRepository.findAllOpenTasks().map { it.toDto() }
    }
}
