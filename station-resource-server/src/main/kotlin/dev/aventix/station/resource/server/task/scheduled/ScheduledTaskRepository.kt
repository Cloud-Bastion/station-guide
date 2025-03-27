package dev.aventix.station.resource.server.task.scheduled

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ScheduledTaskRepository : JpaRepository<ScheduledTaskEntity, UUID>