package dev.aventix.station.authserver.user.role

import dev.aventix.station.authserver.user.User
import dev.aventix.station.authserver.user.authority.UserAuthority
import jakarta.persistence.*
import java.util.UUID

@Entity
class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private lateinit var id: UUID

    lateinit var name: String

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_authorities_map",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")],
    )
    lateinit var authorities: MutableSet<UserAuthority>

    @ManyToMany(
        mappedBy = "roles",
        fetch = FetchType.LAZY,
    )
    lateinit var users: MutableSet<User>

    fun toDto(): UserRoleDto {
        return UserRoleDto(id, name, authorities.map { it.toDto() })
    }

}