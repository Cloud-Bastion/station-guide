package dev.aventix.station.resource.server.expire

import dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryDTO
import dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryService
import dev.aventix.station.resource.server.expire.request.StationExpireProductCreateRequest
import dev.aventix.station.resource.server.expire.request.StationExpireProductPatchRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.NoSuchElementException

@RestController
@RequestMapping("/api/v1/expire")
class StationExpireProductController(
    private val stationExpireProductService: StationExpireProductService,
    private val stationExpireProductCategoryService: StationExpireProductCategoryService
) {
    @GetMapping("/products")
    fun getAllExpiringProducts(): ResponseEntity<List<StationExpireProductDTO>> {
        return ResponseEntity.ok(stationExpireProductService.getAllProductsExpiringOrReduce())
    }

    @PostMapping("/products")
    fun createProduct(@RequestBody createRequest: StationExpireProductCreateRequest): ResponseEntity<StationExpireProductDTO> {
        return try {
            ResponseEntity.ok(stationExpireProductService.create(createRequest))
        } catch (error: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/products/{id}")
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

    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<List<StationExpireProductCategoryDTO>> {
        return ResponseEntity.ok(stationExpireProductCategoryService.getAllCategories())
    }
}
