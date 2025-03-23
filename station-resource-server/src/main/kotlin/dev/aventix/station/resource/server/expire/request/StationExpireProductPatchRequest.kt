package dev.aventix.station.resource.server.expire.request

import java.time.LocalDate
import java.util.UUID

data class StationExpireProductPatchRequest(
    val id: UUID,
    val productId: Long? = null,
    val name: String? = null,
    val categoryId: UUID? = null,
    val reduceProductTime: Long? = null,
    val expireDate: LocalDate? = null,
    val updateLastModifiedDate: Boolean = true,
)