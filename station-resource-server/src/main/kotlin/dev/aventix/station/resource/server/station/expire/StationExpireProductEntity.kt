package dev.aventix.station.resource.server.station.expire

import dev.aventix.station.resource.server.station.expire.category.StationExpireProductCategoryEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "station_expire_item")
class StationExpireProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    lateinit var id: UUID

    @Column(nullable = false, unique = true)
    var productId: Long = 0

    @Column(nullable = false, unique = true)
    lateinit var name: String

    @Column(nullable = true, unique = false)
    var reduceProductTime: Long? = null

    @Column(nullable = true, unique = false)
    var expireDate: LocalDate? = null

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    var category: StationExpireProductCategoryEntity? = null

    fun toDTO(): StationExpireProductDTO {
        return StationExpireProductDTO(
            id = this.id,
            productId = this.productId,
            name = this.name,
            category = this.category?.toDTO(),
            reduceProductTime = this.reduceProductTime,
            expireDate = this.expireDate
        )
    }
}