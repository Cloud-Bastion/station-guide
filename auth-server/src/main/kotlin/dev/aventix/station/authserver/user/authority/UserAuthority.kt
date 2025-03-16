package dev.aventix.station.authserver.user.authority

import dev.aventix.station.authserver.user.User
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "user_authorities")
class UserAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    lateinit var id: UUID

    lateinit var name: String

    @ManyToMany(
        mappedBy = "authorities",
        fetch = FetchType.LAZY,
    )
    lateinit var users: MutableSet<User>

    fun toDto(): UserAuthorityDto =
        UserAuthorityDto(id, name)

}