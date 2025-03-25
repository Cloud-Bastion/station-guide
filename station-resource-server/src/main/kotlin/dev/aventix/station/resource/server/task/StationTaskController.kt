package dev.aventix.station.resource.server.task

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tasks")
class StationTaskController(private val taskService: StationTaskService) {

    @GetMapping("/open-planned")
    fun getOpenPlannedTasks(): ResponseEntity<List<StationTaskDTO>> {
        val openTasks = taskService.findOpenPlannedTasks()
        return ResponseEntity.ok(openTasks)
    }
}
