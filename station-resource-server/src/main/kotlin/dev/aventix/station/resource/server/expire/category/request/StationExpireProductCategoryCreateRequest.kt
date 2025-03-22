package dev.aventix.station.resource.server.expire.category.request

data class StationExpireProductCategoryCreateRequest(
    val name: String,
    val reduceProductTime: Long = 3,
)