package dev.aventix.station.resource.server.task

import dev.aventix.station.resource.server.task.scheduled.StationScheduledTaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface StationTaskRepository : JpaRepository<StationTaskEntity, UUID> {

    @Query("""
        SELECT t FROM StationTaskEntity t
        WHERE t.completed = false
        AND t.isTemplate = false
        AND t.scheduledTask IS NOT NULL
    """)
    fun findOpenPlannedTasks(): List<StationTaskEntity>

    // Add this method to find the latest task instance for a scheduled task
    fun findTopByScheduledTaskOrderByEndTimeDesc(scheduledTask: StationScheduledTaskEntity): StationTaskEntity?

    // New method to find ALL open tasks (both scheduled and temporary)
    @Query("""
        SELECT t FROM StationTaskEntity t
        WHERE t.completed = false
        AND t.isTemplate = false
    """)
    fun findAllOpenTasks(): List<StationTaskEntity>
}
