package dev.aventix.station.resource.server.schedule

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ScheduleEntryService(
    private val repository: ScheduleEntryRepository,
) {

//    fun create(
//        request: ScheduleEntryCreateRequest,
//    ): ScheduleEntry {
//
//    }
//
//    fun getInRange(start: Instant, end: Instant): List<ScheduleEntry> {
//        repository.findByStartGreaterThanEqualAndEndLessThanEqual(start, end)
//    }

}