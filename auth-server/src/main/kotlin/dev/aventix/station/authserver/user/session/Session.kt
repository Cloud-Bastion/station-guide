package dev.aventix.station.authserver.user.session

import dev.aventix.station.authserver.user.User
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.util.*


@Entity
class Session {
    @Id
    var id: UUID = UUID.randomUUID()

    var clientId: String? = null

    var principalName: String? = null

    @Column(nullable = false)
    lateinit var grantType: String

    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    var scopes: Set<String> = emptySet()

    @Column(length = 4000)
    var attributes: String? = null

    var state: String? = null

    @JvmSuppressWildcards
    @OneToMany(mappedBy = "session", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    lateinit var tokens: List<SessionToken>

    @ManyToOne
    @JoinColumn
    lateinit var account: User
}