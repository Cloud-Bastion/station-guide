package dev.aventix.station.authserver.user.authority

import dev.aventix.station.authserver.user.User
import dev.aventix.station.authserver.user.role.UserRole
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "user_authorities")
class UserAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    lateinit var id: UUID

    @Column(unique = true, nullable = false)
    lateinit var name: String

    @ManyToMany(
        mappedBy = "authorities",
        fetch = FetchType.LAZY,
    )
    lateinit var users: MutableSet<User>

    @ManyToMany(
        fetch = FetchType.EAGER,
        mappedBy = "authorities",
    )
    lateinit var roles: MutableSet<UserRole>

    fun toDto(): UserAuthorityDto =
        UserAuthorityDto(id, name)

}