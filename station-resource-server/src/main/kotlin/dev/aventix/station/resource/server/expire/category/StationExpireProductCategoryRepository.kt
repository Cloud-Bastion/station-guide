package dev.aventix.station.resource.server.expire.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StationExpireProductCategoryRepository : JpaRepository<StationExpireProductCategoryEntity, UUID>