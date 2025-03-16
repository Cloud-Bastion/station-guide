package dev.aventix.station.resource.server.station.expire

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StationExpireProductRepository : JpaRepository<StationExpireProductEntity, UUID> {
    fun findByProductId(productId: Long): MutableList<StationExpireProductEntity>
}