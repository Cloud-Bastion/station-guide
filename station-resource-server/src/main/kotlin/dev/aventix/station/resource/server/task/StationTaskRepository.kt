package dev.aventix.station.resource.server.task

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface StationTaskRepository : JpaRepository<StationTaskEntity, UUID>
