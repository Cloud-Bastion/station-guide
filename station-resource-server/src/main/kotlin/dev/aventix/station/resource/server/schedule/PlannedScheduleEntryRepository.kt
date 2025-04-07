package dev.aventix.station.resource.server.schedule

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.UUID

interface PlannedScheduleEntryRepository:
    JpaRepository<PlannedScheduleEntry, UUID>,
    PagingAndSortingRepository<PlannedScheduleEntry, UUID>,
    JpaSpecificationExecutor<PlannedScheduleEntry> {

}