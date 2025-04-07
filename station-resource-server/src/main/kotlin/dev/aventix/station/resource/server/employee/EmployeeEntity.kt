package dev.aventix.station.resource.server.employee

import dev.aventix.station.resource.server.employee.address.EmployeeAddressEntity
import dev.aventix.station.resource.server.schedule.report.MonthlyReport
import dev.aventix.station.resource.server.schedule.stamp.StampEntry
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "station_employee")
class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    lateinit var id: UUID

    @Column(nullable = false, unique = true)
    var badgeNumber: Long = 0

    @Column(nullable = false)
    lateinit var firstName: String

    @Column(nullable = false)
    lateinit var lastName: String

    lateinit var email: String

    @Embedded
    lateinit var address: EmployeeAddressEntity

    @Temporal(TemporalType.DATE)
    lateinit var birthDate: LocalDate

    var taxId: Long = 0

    lateinit var socialSecurityId: String

    var minijob: Boolean = false

    var hourlyWage: Double = 0.0

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_id", nullable = true)
    var createdBy: EmployeeEntity? = null

    @OneToMany(fetch = FetchType.LAZY)
    var timestamps: MutableList<StampEntry> = mutableListOf()

    @OneToMany(fetch = FetchType.LAZY)
    var createdTimestamps: MutableList<StampEntry> = mutableListOf()

    @OneToMany(fetch = FetchType.LAZY)
    var monthlyReports: MutableList<MonthlyReport> = mutableListOf()


    fun toDTO(): EmployeeDTO {
        return EmployeeDTO(
            id,
            badgeNumber.toInt(),
            firstName,
            lastName,
            email,
            address.toDTO(),
            birthDate,
            taxId,
            socialSecurityId,
            createdAt,
            createdBy?.toDTO()
        )
    }

    //TODO: Login data
    //TODO: RFID/NFC ID's
    //TODO: Permissions
}