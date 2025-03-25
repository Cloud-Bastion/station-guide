package dev.aventix.station.resource.server.task.scheduled

import dev.aventix.station.resource.server.task.StationTaskEntity
import dev.aventix.station.resource.server.task.StationTaskRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class StationScheduledTaskInstanceService(
    private val scheduledTaskRepository: StationScheduledTaskRepository,
    private val taskRepository: StationTaskRepository
) {

    @Scheduled(cron = "0 0 * * * *") // Run every hour.  Adjust as needed.
    fun generateTaskInstances() {
        val now = Instant.now()
        val scheduledTasks = scheduledTaskRepository.findAll() // Consider pagination for large datasets

        for (scheduledTask in scheduledTasks) {
            if (!scheduledTask.recurring) {
                continue // Skip non-recurring tasks
            }

            // ---  Implement your recurrence logic here! ---
            // This is a *simplified* example and needs to be adapted
            // to handle different recurrence rules (daily, weekly, etc.)
            // and to avoid creating duplicate tasks.

            val lastTaskInstance = taskRepository.findTopByScheduledTaskOrderByEndTimeDesc(scheduledTask)
            val nextStartTime = lastTaskInstance?.endTime?.plusSeconds(60*60*24) ?: scheduledTask.startTime // + 1 Day, or use recurrenceRule

            if (nextStartTime != null && nextStartTime.isBefore(now)) {
                // Create a new task instance
                val newTaskInstance = StationTaskEntity().apply {
                    this.permissionGroup = scheduledTask.permissionGroup
                    this.endTime = nextStartTime.plusSeconds(60*60)  // Example: 1 hour duration
                    this.isTemplate = false
                    this.scheduledTask = scheduledTask
                    this.completed = false
                }
                taskRepository.save(newTaskInstance)
            }
        }
    }
}
