package dev.aventix.station.resource.server.station.expire

import dev.aventix.station.resource.server.station.expire.category.StationExpireProductCategoryDTO
import dev.aventix.station.resource.server.station.expire.category.StationExpireProductCategoryService
import dev.aventix.station.resource.server.station.expire.category.request.StationExpireProductCategoryCreateRequest
import dev.aventix.station.resource.server.station.expire.request.StationExpireProductCreateRequest
import dev.aventix.station.resource.server.station.expire.request.StationExpireProductPatchRequest
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
    private val stationExpireProductCategoryService: StationExpireProductCategoryService,
) {

    @PostConstruct
    fun init() {
        val categoryB = this.stationExpireProductCategoryService.create(
            StationExpireProductCategoryCreateRequest(
                "B", 5
            )
        )

        val categoryA = this.stationExpireProductCategoryService.create(
            StationExpireProductCategoryCreateRequest(
                "A", 3
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                111111, "Dosenbier", categoryB.id, 3, LocalDate.now().plusDays(10)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                222222, "Birnenbier", categoryB.id, 3, LocalDate.now().plusDays(10)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                333333, "Coca-Cola 250ml", categoryA.id, 3, LocalDate.now().plusDays(10)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                444444, "Apfelsaft", categoryA.id, 3, LocalDate.now().plusDays(10)
            )
        )

        getAllProductsSortedByCategory().forEach { product -> println("Registered: ${product.name} with id: ${product.id}") }
    }

    fun getAllProductsMappedByCategory(): MutableMap<StationExpireProductCategoryDTO, MutableList<StationExpireProductDTO>> {
        return mutableMapOf()/*this.stationExpireProductRepository.findAllWithValidExpireDate()*/
    }

    fun getAllProductsSortedByCategory(): MutableList<StationExpireProductDTO> {
        return this.stationExpireProductRepository.findAll(
            Sort.by(Sort.Order.asc("category.name"), Sort.Order.asc("name"))
        ).map { entity ->
            if (entity.reduceProductTime == null && entity.category != null && entity.category?.reduceProductTime != null) {
                entity.reduceProductTime = entity.category?.reduceProductTime
            }
            entity.toDTO()
        }.toCollection(mutableListOf())
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
                this.expireDate = createRequest.expireDate
            }

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

        return this.stationExpireProductRepository.saveAndFlush(product).toDTO()
    }
}