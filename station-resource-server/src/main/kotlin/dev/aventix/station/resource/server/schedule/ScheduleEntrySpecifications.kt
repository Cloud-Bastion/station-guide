package dev.aventix.station.resource.server.schedule

import dev.aventix.station.resource.server.employee.EmployeeEntity
import dev.aventix.station.resource.server.schedule.event.ScheduleEvent
import dev.aventix.station.resource.server.schedule.event.ScheduleEventType
import org.springframework.data.jpa.domain.Specification
import java.time.Instant
import java.util.UUID
import java.util.function.Predicate

class ScheduleEntrySpecifications {
    companion object {

        fun isPlanned(): Specification<ScheduleEntry> {
            return hasPlanType(ScheduleEntryPlanType.PLANNED)
        }

        fun isActual(): Specification<ScheduleEntry> {
            return hasPlanType(ScheduleEntryPlanType.ACTUAL)
        }

        fun hasPlanType(planType: ScheduleEntryPlanType): Specification<ScheduleEntry> {
            return Specification { root, query, cb ->
                cb.equal(root.get<ScheduleEntryPlanType>("planType"), planType)
            }
        }

        /**
         * Returns only shifts that are assigned to the employee
         * with the given id.
         */
        fun hasAssignee(assigneeId: UUID): Specification<ScheduleEntry> {
            return Specification { root, query, cb ->
                // join employee tables to compare by id not by the entire entity
                val employeeJoin = root.join<ScheduleEntry, EmployeeEntity>("assignee")
                cb.equal(employeeJoin.get<UUID>("id"), assigneeId)
            }
        }

        fun breaksViolated() {}

        /**
         * Returns only shifts that fall into the given time range,
         * where the bounds are including.
         */
        fun isWithinRange(start: Instant, end: Instant): Specification<ScheduleEntry> {
            return Specification { root, query, cb ->
                val join = root.joinCollection<ScheduleEntry, ScheduleEvent>("events")

                val isBoundingEvent = join.get<ScheduleEventType>("eventType").`in`(ScheduleEventType.START, ScheduleEventType.END)
                val inTime = cb.between(join.get("timestamp"), start, end)

                cb.and(isBoundingEvent, inTime)
            }
        }

        fun hasEventWithinRange(type: ScheduleEventType, start: Instant, end: Instant): Specification<ScheduleEntry> {
            return Specification { root, query, cb ->
                val join = root.join<ScheduleEntry, ScheduleEvent>("events")
                cb.and(
                    cb.equal(join.get<ScheduleEventType>("type"), type),
                    cb.between(join.get("timestamp"), start, end)
                )
            }
        }
    }
}