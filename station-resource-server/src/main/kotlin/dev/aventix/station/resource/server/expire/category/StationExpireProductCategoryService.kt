package dev.aventix.station.resource.server.expire.category

import dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryCreateRequest
import dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryDeleteRequest
import dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryPatchRequest
import jakarta.persistence.EntityExistsException
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException
import kotlin.jvm.Throws
import kotlin.jvm.optionals.getOrNull

@Service
class StationExpireProductCategoryService(
    private val stationExpireItemCategoryRepository: dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryRepository,
) {
    fun findCategory(productCategoryId: UUID): Optional<dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryEntity> {
        return this.stationExpireItemCategoryRepository.findById(productCategoryId)
    }

    @Throws(EntityExistsException::class, IllegalArgumentException::class)
    fun create(createRequest: StationExpireProductCategoryCreateRequest): dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryDTO {
        return this.stationExpireItemCategoryRepository.saveAndFlush(
            dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryEntity().apply {
                this.name = createRequest.name
                this.reduceProductTime = createRequest.reduceProductTime
            }).toDTO()
    }

    @Throws(NoSuchElementException::class)
    fun patch(patchRequest: dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryPatchRequest): dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryDTO {
        val productCategory = this.stationExpireItemCategoryRepository.findById(patchRequest.id).getOrNull()
            ?: throw NoSuchElementException("No such product category with id ${patchRequest.id}")

        patchRequest.name?.let { productCategory.name = it }
        patchRequest.reduceProductTime?.let { productCategory.reduceProductTime = it }

        return this.stationExpireItemCategoryRepository.saveAndFlush(productCategory).toDTO()
    }

    @Throws(NoSuchElementException::class)
    fun delete(deleteRequest: StationExpireProductCategoryDeleteRequest) {
        this.stationExpireItemCategoryRepository.deleteById(deleteRequest.id)
    }
}