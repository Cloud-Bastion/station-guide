package dev.aventix.station.resource.server.task

import org.springframework.stereotype.Service

@Service
class StationTaskService(private val taskRepository: StationTaskRepository) {

    fun findOpenPlannedTasks(): List<StationTaskDTO> {
        return taskRepository.findOpenPlannedTasks().map { it.toDto() }
    }
}
