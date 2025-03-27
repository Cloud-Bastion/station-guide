package dev.aventix.station.resource.server.expire.category

import dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryCreateRequest
import dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryDeleteRequest
import dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryPatchRequest
import jakarta.persistence.EntityExistsException
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException
import kotlin.jvm.Throws
import kotlin.jvm.optionals.getOrNull

@Service
class StationExpireProductCategoryService(
    private val stationExpireItemCategoryRepository: StationExpireProductCategoryRepository,
) {
    fun findCategory(productCategoryId: UUID): Optional<StationExpireProductCategoryEntity> {
        return this.stationExpireItemCategoryRepository.findById(productCategoryId)
    }

    @Throws(EntityExistsException::class, IllegalArgumentException::class)
    fun create(createRequest: StationExpireProductCategoryCreateRequest): StationExpireProductCategoryDTO {
        return this.stationExpireItemCategoryRepository.saveAndFlush(
            StationExpireProductCategoryEntity().apply {
                this.name = createRequest.name
                this.reduceProductTime = createRequest.reduceProductTime
            }).toDTO()
    }

    @Throws(NoSuchElementException::class)
    fun patch(patchRequest: StationExpireProductCategoryPatchRequest): StationExpireProductCategoryDTO {
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

    fun getAllCategories(): List<StationExpireProductCategoryDTO> {
        return stationExpireItemCategoryRepository.findAll(Sort.by(Sort.Order.asc("name"))).map { it.toDTO() }
    }
}
