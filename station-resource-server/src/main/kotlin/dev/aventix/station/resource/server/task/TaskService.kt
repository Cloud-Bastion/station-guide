package dev.aventix.station.resource.server.task

import dev.aventix.station.resource.server.task.scheduled.ScheduledTaskEntity
import dev.aventix.station.resource.server.task.scheduled.ScheduledTaskRepository
import org.jetbrains.annotations.Async.Schedule
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class TaskService(
    private val scheduledTaskRepository: ScheduledTaskRepository,
    private val taskRepository: TaskRepository,
) {
    fun getAllOpenTasks(): MutableList<TaskDTO> {
        return this.taskRepository.findAllByIsTemplate(false).map(TaskEntity::toDTO).toCollection(mutableListOf())
    }

    //every 30 minutes
    @Scheduled(fixedRate = 1000 * 60 * 30, initialDelay = 0)
    fun checkTask() {
        this.scheduledTaskRepository.findAll().forEach {
            if (hasToCreateTemp(it)) {
                this.taskRepository.saveAndFlush(TaskEntity().apply {
                    this.title = it.template.title
                    it.template.description?.let { this.description = it }
                    this.priority = it.template.priority
                    it.template.permissionGroup?.let { this.permissionGroup = it }
                    it.template.createdBy?.let { this.createdBy = it }
                    this.startTime = LocalDateTime.of(LocalDate.now(), it.startTime)

                    it.endTime?.let { endTime ->
                        this.endTime = LocalDateTime.of(LocalDate.now().plusDays(it.endTimeDaysAdd.toLong()), endTime)
                    }
                    this.completed = false
                    this.isTemplate = false
                })

                it.lastCreatedTask = LocalDateTime.now()
                this.scheduledTaskRepository.saveAndFlush(it)
            }
        }
    }

    private fun hasToCreateTemp(task: ScheduledTaskEntity): Boolean {
        val today: LocalDate = LocalDate.now()

        if (task.lastCreatedTask != null && task.lastCreatedTask!!.toLocalDate() == today) {
            return false
        }

        val isTodayDayOfWeek: Boolean = task.daysOfWeek.contains(today.dayOfWeek.value)
        val isTodayDayOfMonth: Boolean =
            task.daysOfMonth.contains(today.dayOfMonth) || (task.daysOfMonth.contains(32) && today.dayOfMonth == today.lengthOfMonth())

        if (task.frequency == "DAILY") return true
        if (task.frequency == "WEEKLY") return isTodayDayOfWeek
        if (task.frequency == "MONTHLY") return isTodayDayOfMonth

        return false
    }
}