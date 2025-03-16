package dev.aventix.station.resource.server.station.expire

import dev.aventix.station.resource.server.station.expire.category.StationExpireProductCategoryDTO
import dev.aventix.station.resource.server.station.expire.request.StationExpireProductCreateRequest
import dev.aventix.station.resource.server.station.expire.request.StationExpireProductPatchRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.NoSuchElementException

@RestController
@RequestMapping("/api/v1/expire/products")
class StationExpireProductController(
    private val stationExpireProductService: StationExpireProductService,
) {

    /*@GetMapping
    fun getAllProducts(): ResponseEntity<List<StationExpireProductDTO>> {
        return ResponseEntity.ok(stationExpireProductService.getAllProductsSortedByCategory())
    }*/

    @GetMapping
    fun getAllProducts(): ResponseEntity<Map<StationExpireProductCategoryDTO, List<StationExpireProductDTO>>> {
        return ResponseEntity.ok(stationExpireProductService.getAllProductsMappedByCategory())
    }

    @PostMapping
    fun createProduct(@RequestBody createRequest: StationExpireProductCreateRequest): ResponseEntity<StationExpireProductDTO> {
        return try {
            ResponseEntity.ok(stationExpireProductService.create(createRequest))
        } catch (error: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/{id}")
    fun patchProduct(
        @PathVariable id: UUID,
        @RequestBody patchRequest: StationExpireProductPatchRequest,
    ): ResponseEntity<StationExpireProductDTO> {
        return try {
            ResponseEntity.ok(stationExpireProductService.patch(patchRequest))
        } catch (error: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}
