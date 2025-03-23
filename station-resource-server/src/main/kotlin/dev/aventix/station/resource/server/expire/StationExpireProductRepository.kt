package dev.aventix.station.resource.server.expire

import jakarta.persistence.OrderBy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StationExpireProductRepository : JpaRepository<StationExpireProductEntity, UUID> {
    fun findByProductId(productId: Long): MutableList<StationExpireProductEntity>

    @Query(
        """
        SELECT product FROM StationExpireProductEntity product
        LEFT JOIN StationExpireProductCategoryEntity category ON product.category.id = category.id
        WHERE product.expireDate <= CURRENT_DATE + (COALESCE(product.reduceProductTime, category.reduceProductTime, 0)) day
        OR product.expireDate IS NULL
        ORDER BY category.name ASC, product.name ASC
    """
    )
    fun findAllWithInvalidExpireDate(): List<StationExpireProductEntity>
}