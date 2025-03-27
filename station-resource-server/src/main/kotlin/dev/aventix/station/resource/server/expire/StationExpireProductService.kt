package dev.aventix.station.resource.server.expire

import dev.aventix.station.resource.server.expire.category.request.StationExpireProductCategoryCreateRequest
import dev.aventix.station.resource.server.expire.request.StationExpireProductCreateRequest
import dev.aventix.station.resource.server.expire.request.StationExpireProductPatchRequest
import jakarta.annotation.PostConstruct
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import kotlin.jvm.Throws
import kotlin.jvm.optionals.getOrNull

@Service
class StationExpireProductService(
    private val stationExpireProductRepository: StationExpireProductRepository,
    private val stationExpireProductCategoryService: dev.aventix.station.resource.server.expire.category.StationExpireProductCategoryService,
) {
    fun getAllProductsSortedByName(): MutableList<StationExpireProductDTO> {
        return this.stationExpireProductRepository.findAll(Sort.by(Sort.Order.asc("name")))
            .map(StationExpireProductEntity::toDTO).toCollection(
                mutableListOf()
            )
    }

    fun getAllProductsSortedByCategory(): MutableList<StationExpireProductDTO> {
        return this.stationExpireProductRepository.findAll(
            Sort.by(Sort.Order.desc("category.name"), Sort.Order.asc("name"))
        ).map { entity ->
            entity.toDTO()
        }.toCollection(mutableListOf())
    }

    fun getAllProductsExpiringOrReduce(): MutableList<StationExpireProductDTO> {
        return this.stationExpireProductRepository.findAllWithInvalidExpireDate().map(StationExpireProductEntity::toDTO)
            .toCollection(mutableListOf())
    }

    @Throws(NoSuchElementException::class)
    fun create(createRequest: StationExpireProductCreateRequest): StationExpireProductDTO {
        return this.stationExpireProductRepository.saveAndFlush(StationExpireProductEntity().apply {
            this.productId = createRequest.productId
            this.name = createRequest.name
            createRequest.productCategoryId?.let {
                this.category = stationExpireProductCategoryService.findCategory(it).getOrNull()
                    ?: throw NoSuchElementException("No categories found for id ${createRequest.productCategoryId}")
            }

            createRequest.reduceProductTime?.let {
                this.reduceProductTime = it
            }

            createRequest.expireDate?.let {
                this.expireDate = it
            }

            this.lastUpdateDate = null

        }).toDTO()
    }

    @Throws(NoSuchElementException::class)
    fun patch(patchRequest: StationExpireProductPatchRequest): StationExpireProductDTO {
        val product = (this.stationExpireProductRepository.findById(patchRequest.id).getOrNull()
            ?: throw NoSuchElementException("No product with id ${patchRequest.id}"))

        patchRequest.productId?.let {
            product.productId = it
        }

        patchRequest.name?.let {
            product.name = it
        }

        patchRequest.categoryId?.let {
            val findCategory = stationExpireProductCategoryService.findCategory(it)
            if (!findCategory.isPresent) throw NoSuchElementException("No category with id ${patchRequest.categoryId} exists")
            product.category = findCategory.get()
        }

        patchRequest.reduceProductTime?.let {
            product.reduceProductTime = it
        }

        patchRequest.expireDate?.let {
            product.expireDate = it
        }

        if (patchRequest.updateLastModifiedDate) product.lastUpdateDate = LocalDate.now()
        else product.lastUpdateDate = null

        return this.stationExpireProductRepository.saveAndFlush(product).toDTO()
    }
}