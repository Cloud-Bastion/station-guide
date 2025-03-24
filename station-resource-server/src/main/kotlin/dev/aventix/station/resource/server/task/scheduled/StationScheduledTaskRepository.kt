package dev.aventix.station.resource.server.task.scheduled

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StationScheduledTaskRepository : JpaRepository<StationScheduledTaskEntity, UUID>
