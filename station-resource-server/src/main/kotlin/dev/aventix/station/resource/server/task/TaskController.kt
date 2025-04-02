package dev.aventix.station.resource.server.task

import dev.aventix.station.resource.server.task.request.CloseOrOpenTaskRequest
import dev.aventix.station.resource.server.task.request.TaskCreateRequest
import dev.aventix.station.resource.server.task.scheduled.ScheduledTaskDTO
import dev.aventix.station.resource.server.task.scheduled.ScheduledTaskService
import dev.aventix.station.resource.server.task.scheduled.request.ScheduledTaskCreateRequest
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController(
    private val taskService: TaskService,
    private val scheduledTaskService: ScheduledTaskService,
) {
    @GetMapping("/open_tasks")
    fun getOpenTasks(): ResponseEntity<List<TaskDTO>> {
        return ResponseEntity.ok(this.taskService.getAllOpenTasks())
    }

    @PostMapping("/close")
    fun close(@RequestBody closeTaskRequest: CloseOrOpenTaskRequest): ResponseEntity<TaskDTO> {
        return try {
            ResponseEntity.ok(this.taskService.closeOrOpenTask(closeTaskRequest))
        } catch (error: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/open")
    fun open(@RequestBody openTaskRequest: CloseOrOpenTaskRequest): ResponseEntity<TaskDTO> {
        return try {
            ResponseEntity.ok(this.taskService.closeOrOpenTask(openTaskRequest))
        } catch (error: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun create(@RequestBody createTaskRequest: TaskCreateRequest): ResponseEntity<TaskDTO> {
        return ResponseEntity.ok(this.taskService.createTask(createTaskRequest))
    }

    @PostMapping("/scheduled")
    fun createScheduled(@RequestBody createScheduledTaskRequest: ScheduledTaskCreateRequest): ResponseEntity<ScheduledTaskDTO> {
        return ResponseEntity.ok(this.scheduledTaskService.create(createScheduledTaskRequest))
    }
}