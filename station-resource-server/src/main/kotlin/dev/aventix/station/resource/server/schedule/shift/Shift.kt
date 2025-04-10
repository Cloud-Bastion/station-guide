package dev.aventix.station.resource.server.schedule.shift

import dev.aventix.station.resource.server.schedule.stamp.StampEntryDto
import dev.aventix.station.resource.server.schedule.stamp.StampEntryType

class Shift private constructor(
    private val stamps: List<StampEntryDto>
) {

    companion object {
        /**
         * Compiles a list of only full shifts represented by the given
         * stamp set. If the stamp set ends with a shift that is not finished
         * (= the stamp set does not end with an END stamp), these stamps
         * will be cropped.
         */
        @Throws(IllegalArgumentException::class)
        fun fromStampSet(stamps: List<StampEntryDto>): List<Shift> {
            if (stamps.size < 2) {
                return emptyList()
            }

            val sorted = StampEntryDto.sorted(stamps)
            var sortedList = sorted.toList()
            val output = mutableListOf<Shift>()

            // crop all stamps that indicate an unfinished shift.
            if (sortedList.last().eventType != StampEntryType.END) {
                sortedList = sortedList.dropLastWhile {
                    it.eventType != StampEntryType.END
                }
            }

            while (sortedList.isNotEmpty()) {
                val shiftStamps = sortedList.takeWhile { it.eventType != StampEntryType.END }.toMutableList()
                sortedList = sortedList.dropWhile { it.eventType != StampEntryType.END }
                shiftStamps.add(sortedList.first())
                sortedList.removeFirst()
                output.add(Shift(shiftStamps))
            }

            // 01.04 | 8:00
            // 28.02 | 22:00   Kommen
            // 01.03 | 0:00    6:00 Gehen
            // 31.03 | 23:59
            //
            //

            return output
        }
    }

    fun start(): StampEntryDto {
        return stamps.first()
    }

    fun end(): StampEntryDto {
        return stamps.last()
    }

    override fun toString(): String {
        return "Shift(stamps=$stamps)"
    }

}