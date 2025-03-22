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
                111111, "Dosenbier", categoryB.id, null, LocalDate.now().plusDays(6)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                555555, "Flaschenbier", categoryB.id, 6, LocalDate.now().plusDays(7)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                222222, "Birnenbier", categoryB.id, null, LocalDate.now().plusDays(7)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                333333, "Coca-Cola 250ml", categoryB.id, null, LocalDate.now().plusDays(3)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                444444, "Apfelsaft", categoryA.id, null, LocalDate.now().plusDays(2)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                666666, "Ananassaft", categoryA.id, 4, LocalDate.now().plusDays(1)
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                777777, "Apfelkuchen", null, null, LocalDate.now()
            )
        )

        this.create(
            StationExpireProductCreateRequest(
                888888, "Torte", null, null, null
            )
        )


        getAllProductsSortedByCategory().forEach { product -> println("Registered: ${product.name} with id: ${product.id}") }
        this.getAllProductsExpiringOrReduce().forEach { product -> println("Sort out: ${product.name} with id: ${product.id}") }
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

    fun getAllProductsExpiringOrReduce(): MutableList<StationExpireProductDTO> {
        return this.stationExpireProductRepository.findAllWithInvalidExpireDate()
            .map(StationExpireProductEntity::toDTO)
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