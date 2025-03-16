package dev.aventix.station.resource.server.station.expire.request

import java.time.LocalDate
import java.util.UUID

data class StationExpireProductCreateRequest(
    val productId: Long,
    val name: String,
    val productCategoryId: UUID? = null,
    val reduceProductTime: Long? = null,
    val expireDate: LocalDate? = null
)