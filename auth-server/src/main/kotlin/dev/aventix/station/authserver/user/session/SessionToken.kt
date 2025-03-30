package dev.aventix.station.authserver.user.session

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import org.springframework.security.oauth2.core.OAuth2Token
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Entity
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class SessionToken {
    @Id
    open var id = UUID.randomUUID()

    @Column(name = "`value`", length = 4000)
    open var value: String? = null

    open var issued: LocalDateTime? = null

    open var expires: LocalDateTime? = null

    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    open var metadata: Map<String, Any> = emptyMap()

    @ManyToOne
    @JoinColumn
    open var session: Session? = null

    open fun readFromOAuth2Token(token: OAuth2Token) {
        value = token.tokenValue
        issued = token.issuedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
        expires = token.expiresAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
    }

    open abstract fun writeToOAuth2Token(): Pair<OAuth2Token, Map<String, Any>>
}