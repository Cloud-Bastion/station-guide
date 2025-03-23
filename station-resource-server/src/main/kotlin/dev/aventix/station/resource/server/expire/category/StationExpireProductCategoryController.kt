package dev.aventix.station.resource.server.expire.category

import dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryCreateRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/expire")
class StationExpireProductCategoryController(
    private val stationExpireProductCategoryService: StationExpireProductCategoryService,
) {
    @PostMapping("/category")
    fun createCategory(@RequestBody createRequest: StationExpireProductCategoryCreateRequest): ResponseEntity<StationExpireProductCategoryDTO> {
        return try {
            ResponseEntity.ok(stationExpireProductCategoryService.create(createRequest))
        } catch (error: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/category")
    fun getAllCategories(): ResponseEntity<List<StationExpireProductCategoryDTO>> {
        return ResponseEntity.ok(stationExpireProductCategoryService.getAllCategories())
    }
}