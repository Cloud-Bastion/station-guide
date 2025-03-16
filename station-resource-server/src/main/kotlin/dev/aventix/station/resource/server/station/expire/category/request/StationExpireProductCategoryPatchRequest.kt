package dev.aventix.station.resource.server.station.expire.category.request

import java.util.*

data class StationExpireProductCategoryPatchRequest(
    val id: UUID,
    val name: String? = null,
    var reduceProductTime: Long? = null
)