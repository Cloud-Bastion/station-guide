package dev.aventix.station.resource.server.task

import dev.aventix.station.resource.server.task.request.CloseOrOpenTaskRequest
import dev.aventix.station.resource.server.task.request.TaskCreateRequest
import dev.aventix.station.resource.server.task.scheduled.ScheduledTaskEntity
import dev.aventix.station.resource.server.task.scheduled.ScheduledTaskRepository
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration

@Service
class TaskService(
    private val scheduledTaskRepository: ScheduledTaskRepository,
    private val taskRepository: TaskRepository,
) {
    // @PostConstruct
    fun init() {
        var test1 = ZonedDateTime.of(2025, 3, 30,0,55,0,0, ZoneId.of("Europe/Berlin"))
        var test2 = ZonedDateTime.of(2025, 3, 30,6,55,0,0, ZoneId.of("Europe/Berlin"))

//        val zoned1 = test1.atZoneSameInstant(ZoneId.of("Europe/Berlin"))
//        val zoned2 = test2.atZoneSameInstant(ZoneId.of("Europe/Berlin"))
        val save = test1.toOffsetDateTime()
        val convertBack = save.atZoneSameInstant(ZoneId.of("Europe/Berlin"))

        var duration = java.time.Duration.between(convertBack, test2)

        println("Duration between: ${duration.toHours()}")
    }

    fun getAllOpenTasks(): MutableList<TaskDTO> {
        return this.taskRepository.findAllByIsTemplateAndCompleted(isTemplate = false, completed = false)
            .map(TaskEntity::toDTO).toCollection(mutableListOf())
    }

    @Throws(EntityNotFoundException::class)
    fun closeOrOpenTask(request: CloseOrOpenTaskRequest): TaskDTO {
        val taskEntity = this.taskRepository.findById(request.id).getOrNull()
            ?: throw EntityNotFoundException("Task with id " + request.id + " not found")
        taskEntity.completed = request.state
        return this.taskRepository.saveAndFlush(taskEntity).toDTO()
    }

    fun createTask(request: TaskCreateRequest): TaskDTO {
        return this.taskRepository.saveAndFlush(TaskEntity().apply {
            this.title = request.title
            request.description?.let { this.description = it }
            this.priority = request.priority ?: 0
            request.permissionGroup?.let { this.permissionGroup = it }
            request.createdBy?.let { this.createdBy = it }
            request.startTime?.let { this.startTime = it }
            request.endTime?.let { this.endTime = it }
            this.completed = false
            this.isTemplate = false
        }).toDTO()
    }

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