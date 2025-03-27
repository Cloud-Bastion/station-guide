package dev.aventix.station.resource.server.task

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TaskRepository : JpaRepository<TaskEntity, UUID> {
    fun findAllByIsTemplate(template: Boolean): MutableList<TaskEntity>
}