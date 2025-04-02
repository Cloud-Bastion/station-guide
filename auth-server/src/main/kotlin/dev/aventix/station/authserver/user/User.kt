package dev.aventix.station.authserver.user

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.aventix.station.authserver.provider.AuthProvider
import dev.aventix.station.authserver.user.authority.UserAuthority
import dev.aventix.station.authserver.user.session.Session
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "security_users")
class User {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    //@Column(nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @Column(unique = true, nullable = false)
    var badgeNumber: Long = 0

    lateinit var email: String

    lateinit var firstName: String

    lateinit var lastName: String

    lateinit var password: String

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authorities_map",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")],
    )
    lateinit var authorities: MutableSet<UserAuthority>

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    lateinit var authProvider: AuthProvider

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var sessions: MutableList<Session> = mutableListOf()

    @Column(nullable = true)
    var oAuthId: String? = null

    fun toDto(): UserDto =
        UserDto(id,
            badgeNumber,
            email,
            firstName,
            lastName,
            password,
            authorities.map { authority -> authority.toDto() }.toSet(),
            authProvider,
            oAuthId,
            sessions
        )

}