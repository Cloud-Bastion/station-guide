package dev.aventix.station.resource.server.station.expire.category

import java.util.UUID

data class StationExpireProductCategoryDTO(
    val id: UUID,
    val name: String,
    val reduceProductTime: Long? = null,
)