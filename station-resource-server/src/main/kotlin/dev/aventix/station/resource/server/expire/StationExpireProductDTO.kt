package dev.aventix.station.resource.server.expire

import dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryDTO
import java.time.LocalDate
import java.util.*

data class StationExpireProductDTO(
    val id: UUID,
    val productId: Long,
    val name: String,
    val category: StationExpireProductCategoryDTO?,
    val reduceProductTime: Long?,
    val expireDate: LocalDate?,
    val lastUpdateDate: LocalDate?
)