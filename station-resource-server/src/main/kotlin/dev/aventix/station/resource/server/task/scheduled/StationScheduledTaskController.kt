package dev.aventix.station.resource.server.task.scheduled

import jakarta.annotation.PostConstruct
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/v1/tasks")
class StationScheduledTaskController(private val scheduledTaskService: StationScheduledTaskService) {

    @PostMapping("/scheduled")
    fun createScheduledTask(@RequestBody request: StationScheduledTaskCreateRequest): ResponseEntity<StationScheduledTaskDTO> {
        val createdTask = scheduledTaskService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask)
    }

}
