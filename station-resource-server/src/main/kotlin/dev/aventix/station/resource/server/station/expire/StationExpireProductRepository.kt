package dev.aventix.station.resource.server.station.expire

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StationExpireProductRepository : JpaRepository<StationExpireProductEntity, UUID> {
    fun findByProductId(productId: Long): MutableList<StationExpireProductEntity>

    @Query(
        """
        SELECT p FROM StationExpireProductEntity p 
        WHERE 
            (p.expireDate - COALESCE(p.reduceProductTime, p.category.reduceProductTime, 0)) = CURRENT_DATE
            OR (p.expireDate IS NULL AND COALESCE(p.reduceProductTime, p.category.reduceProductTime, 0) = 0)
    """
    )
    fun findAllWithValidExpireDate(): List<StationExpireProductEntity>
}