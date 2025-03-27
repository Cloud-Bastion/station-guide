package dev.aventix.station.resource.server.task

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController(
    private val taskService: TaskService,
) {
    @GetMapping("/open_tasks")
    fun getOpenTasks(): ResponseEntity<List<TaskDTO>> {
        return ResponseEntity.ok(this.taskService.getAllOpenTasks())
    }
}