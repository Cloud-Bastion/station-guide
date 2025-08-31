package dev.aventix.station.authserver.user

import dev.aventix.station.authserver.user.authority.UserAuthority
import dev.aventix.station.authserver.user.role.UserRole
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "security_users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @Column(unique = true, nullable = false)
    var badgeNumber: Long = 0

    @Column(nullable = false)
    lateinit var email: String

    @Column(nullable = false)
    lateinit var firstName: String

    @Column(nullable = false)
    lateinit var lastName: String

    @Column(nullable = true)
    lateinit var password: String

    @Column(nullable = false)
    var passwordChanged: Boolean = false

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authorities_map",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")],
    )
    lateinit var authorities: MutableSet<UserAuthority>

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles_map",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")],
    )
    lateinit var roles: MutableSet<UserRole>

    fun toDto(): UserDto =
        UserDto(id,
            badgeNumber,
            email,
            firstName,
            lastName,
            password,
            passwordChanged,
            authorities.map { authority -> authority.toDto() }.toSet(),
            roles.map { it.toDto() }.toSet()
        )

}