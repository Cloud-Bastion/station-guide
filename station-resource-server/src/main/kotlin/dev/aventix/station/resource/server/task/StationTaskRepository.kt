package dev.aventix.station.resource.server.task

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
}
