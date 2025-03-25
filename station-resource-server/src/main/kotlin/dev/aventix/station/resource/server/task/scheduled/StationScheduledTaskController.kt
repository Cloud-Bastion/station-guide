package dev.aventix.station.resource.server.task.scheduled

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/scheduled")
class StationScheduledTaskController(private val scheduledTaskService: StationScheduledTaskService) {

    @PostMapping
    fun createScheduledTask(@RequestBody request: StationScheduledTaskCreateRequest): ResponseEntity<StationScheduledTaskDTO> {
        val createdTask = scheduledTaskService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask)
    }
}
