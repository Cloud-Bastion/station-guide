package dev.aventix.station.resource.server.expire

import dev.aventix.station.resource.server.expire.request.StationExpireProductCreateRequest
import dev.aventix.station.resource.server.expire.request.StationExpireProductDeleteRequest
import dev.aventix.station.resource.server.expire.request.StationExpireProductPatchRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/expire")
class StationExpireProductController(
    private val stationExpireProductService: StationExpireProductService
) {
    @GetMapping("/products")
    fun getAllExpiringProducts(): ResponseEntity<List<StationExpireProductDTO>> {
        return ResponseEntity.ok(
            stationExpireProductService.getAllProductsExpiringOrReduce()
        )
    }

    @PostMapping("/products")
    fun createProduct(@RequestBody createRequest: StationExpireProductCreateRequest): ResponseEntity<StationExpireProductDTO> {
        return ResponseEntity.ok(
            stationExpireProductService.create(createRequest)
        )
    }

    @PatchMapping("/products/{id}")
    fun patchProduct(
        @PathVariable id: UUID,
        @RequestBody patchRequest: StationExpireProductPatchRequest
    ): ResponseEntity<StationExpireProductDTO> {
        // Ensure the ID from the path variable is used
        val updatedProduct = stationExpireProductService.patch(patchRequest.copy(id = id))
        return ResponseEntity.ok(updatedProduct)
    }

    @GetMapping("/all-products")
    fun getAllProducts(): ResponseEntity<List<StationExpireProductDTO>> {
        return ResponseEntity.ok(stationExpireProductService.getAllProductsSortedByCategory())
    }
}
