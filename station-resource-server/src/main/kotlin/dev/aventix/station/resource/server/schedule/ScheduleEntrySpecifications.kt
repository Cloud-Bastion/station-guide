package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeEntity
import dev.aventix.station.resource.server.schedule.stamp.StampEntry
import dev.aventix.station.resource.server.schedule.stamp.StampEntryType
import org.springframework.data.jpa.domain.Specification
import java.time.Instant
import java.util.UUID

class ScheduleEntrySpecifications {
    companion object {

        /**
         * Returns only shifts that are assigned to the employee
         * with the given id.
         */
        fun hasAssignee(assigneeId: UUID): Specification<PlannedScheduleEntry> {
            return Specification { root, query, cb ->
                // join employee tables to compare by id not by the entire entity
                val employeeJoin = root.join<PlannedScheduleEntry, EmployeeEntity>("assignee")
                cb.equal(employeeJoin.get<UUID>("id"), assigneeId)
            }
        }

        /**
         * Returns only shifts that fall into the given time range,
         * where the bounds are including.
         */
        fun isWithinRange(start: Instant, end: Instant): Specification<PlannedScheduleEntry> {
            return Specification { root, query, cb ->
                val join = root.joinCollection<PlannedScheduleEntry, StampEntry>("events")

                val isBoundingEvent = join.get<StampEntryType>("eventType").`in`(StampEntryType.START, StampEntryType.END)
                val inTime = cb.between(join.get("timestamp"), start, end)

                cb.and(isBoundingEvent, inTime)
            }
        }

        fun hasEventWithinRange(type: StampEntryType, start: Instant, end: Instant): Specification<PlannedScheduleEntry> {
            return Specification { root, query, cb ->
                val join = root.join<PlannedScheduleEntry, StampEntry>("events")
                cb.and(
                    cb.equal(join.get<StampEntryType>("type"), type),
                    cb.between(join.get("timestamp"), start, end)
                )
            }
        }
    }
}