package dev.aventix.station.resource.server.expire.category

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "station_expire_item_category")
class StationExpireProductCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    lateinit var id: UUID

    @Column(nullable = false, unique = true)
    lateinit var name: String

    @Column(nullable = true, unique = false)
    var reduceProductTime: Long? = null

    //TODO: Maybe add icon here to customize self item categories

    fun toDTO(): StationExpireProductCategoryDTO {
        return StationExpireProductCategoryDTO(id, name, reduceProductTime)
    }
}