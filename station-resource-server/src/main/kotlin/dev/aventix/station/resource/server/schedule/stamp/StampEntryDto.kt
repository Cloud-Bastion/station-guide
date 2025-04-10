package dev.aventix.station.resource.server.schedule.stamp

import java.time.ZonedDateTime
import java.util.UUID
import java.util.stream.Stream

data class StampEntryDto(
    val id: UUID,
    val assigneeId: UUID,
    val timestamp: ZonedDateTime,
    val eventType: StampEntryType,
) {
    companion object {
        fun sorted(coll: Collection<StampEntryDto>): Stream<StampEntryDto> {
            return coll.stream().sorted { stamp1, stamp2 ->
                if (stamp1.timestamp.isBefore(stamp2.timestamp)) {
                    return@sorted -1
                } else if (stamp1.timestamp.isAfter(stamp2.timestamp)) {
                    return@sorted 1
                } else {
                    return@sorted 0
                }
            }
        }
    }

    override fun toString(): String {
        return "StampEntryDto(id=$id, assigneeId=$assigneeId, timestamp=$timestamp, eventType=$eventType)"
    }
}